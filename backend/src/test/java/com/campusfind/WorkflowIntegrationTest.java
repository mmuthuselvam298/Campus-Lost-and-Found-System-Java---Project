package com.campusfind;

import com.campusfind.dto.ClaimDtos;
import com.campusfind.dto.FoundReportDtos;
import com.campusfind.dto.PickupDtos;
import com.campusfind.entity.*;
import com.campusfind.repository.CampusLocationRepository;
import com.campusfind.repository.UserRepository;
import com.campusfind.service.ClaimService;
import com.campusfind.service.PickupService;
import com.campusfind.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WorkflowIntegrationTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private PickupService pickupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampusLocationRepository campusLocationRepository;

    @Test
    @DisplayName("Complete flow: Found Report -> Privacy Masking -> Claim -> Approval -> Pickup QR/OTP -> Collected Receipt")
    void testCompleteLostAndFoundLifecycle() {
        // 1. Get seeded users and location
        User student = userRepository.findByEmail("student@campus.edu").orElseThrow();
        User staff = userRepository.findByEmail("staff@campus.edu").orElseThrow();
        CampusLocation location = campusLocationRepository.findAll().get(0);

        // 2. Submit Found Report with private anti-fraud details
        FoundReportDtos.CreateFoundReportRequest foundReq = new FoundReportDtos.CreateFoundReportRequest();
        foundReq.setTitle("Graphing Calculator TI-84 Plus");
        foundReq.setCategory("Electronics");
        foundReq.setColor("Black");
        foundReq.setBrand("Texas Instruments");
        foundReq.setLocationId(location.getId());
        foundReq.setSpecificArea("Room 304 Desk");
        foundReq.setFoundDate(LocalDateTime.now().minusHours(2));
        foundReq.setPublicDescription("Found TI-84 calculator on classroom desk.");
        foundReq.setPrivateVerificationDetails("Engraved with name 'Taylor M' on slide cover, battery door missing clip.");

        FoundReportDtos.FoundReportResponse foundResp = reportService.createFoundReport(foundReq, staff.getId(), "127.0.0.1");
        assertNotNull(foundResp.getId());
        assertTrue(foundResp.getReferenceId().startsWith("LF-"));
        assertTrue(foundResp.isHasPrivateDetails());

        // 3. Verify Privacy Masking: A student viewing the report CANNOT see private verification details
        FoundReportDtos.FoundReportResponse studentView = reportService.getFoundReportById(foundResp.getId(), student.getId(), Role.ROLE_STUDENT);
        assertNull(studentView.getPrivateVerificationDetails(), "Private verification details must be masked for students to prevent fraud!");

        // 4. Submit Claim with blind verification answers
        ClaimDtos.CreateClaimRequest claimReq = new ClaimDtos.CreateClaimRequest();
        claimReq.setFoundReportId(foundResp.getId());
        claimReq.setClaimantAnswers("Slide cover has my name 'Taylor M' engraved, and the battery clip is missing.");

        ClaimDtos.ClaimResponse claimResp = claimService.submitClaim(claimReq, student.getId(), "127.0.0.1");
        assertNotNull(claimResp.getId());
        assertEquals("HIGH", claimResp.getConsistencyScore(), "AI should identify high verification alignment with private tokens");
        assertEquals(ClaimStatus.SUBMITTED, claimResp.getStatus());

        // 5. Staff reviews and approves claim
        ClaimDtos.ReviewClaimRequest reviewReq = new ClaimDtos.ReviewClaimRequest();
        reviewReq.setStatus(ClaimStatus.APPROVED);
        reviewReq.setAdminNotes("Name verified against class enrollment list.");
        reviewReq.setStorageLocation("Office Locker 12");

        ClaimDtos.ClaimResponse reviewed = claimService.reviewClaim(claimResp.getId(), reviewReq, staff.getId(), "127.0.0.1");
        assertEquals(ClaimStatus.APPROVED, reviewed.getStatus());

        // 6. Student schedules Pickup Appointment
        PickupDtos.SchedulePickupRequest pickupReq = new PickupDtos.SchedulePickupRequest();
        pickupReq.setClaimId(reviewed.getId());
        pickupReq.setScheduledDate(LocalDateTime.now().plusDays(1));
        pickupReq.setTimeSlot("11:00 - 11:30 AM");

        PickupDtos.PickupAppointmentResponse appt = pickupService.schedulePickup(pickupReq, student.getId(), "127.0.0.1");
        assertNotNull(appt.getId());
        assertNotNull(appt.getQrToken());
        assertNotNull(appt.getOtpCode());
        assertEquals(6, appt.getOtpCode().length());
        assertNotNull(appt.getQrCodeBase64());
        assertTrue(appt.getQrCodeBase64().startsWith("data:image/png;base64,"));

        // 7. Staff verifies pickup using 6-digit OTP code
        PickupDtos.VerifyPickupRequest verifyReq = new PickupDtos.VerifyPickupRequest();
        verifyReq.setOtpCode(appt.getOtpCode());
        verifyReq.setStaffNotes("Student identity checked and verified.");

        PickupDtos.CollectionReceiptDto receipt = pickupService.verifyAndCompletePickup(verifyReq, staff.getId(), "127.0.0.1");
        assertNotNull(receipt.getReceiptNumber());
        assertTrue(receipt.getReceiptNumber().startsWith("RCPT-"));
        assertEquals(student.getFullName(), receipt.getClaimantName());
        assertNotNull(receipt.getDigitalSignature());

        // 8. Verify Found item is marked RETURNED
        FoundReportDtos.FoundReportResponse finalReport = reportService.getFoundReportById(foundResp.getId(), staff.getId(), Role.ROLE_STAFF);
        assertEquals(ReportStatus.RETURNED, finalReport.getStatus());
    }
}
