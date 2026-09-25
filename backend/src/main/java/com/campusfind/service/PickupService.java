package com.campusfind.service;

import com.campusfind.dto.PickupDtos;
import com.campusfind.entity.*;
import com.campusfind.repository.ClaimRepository;
import com.campusfind.repository.FoundReportRepository;
import com.campusfind.repository.PickupAppointmentRepository;
import com.campusfind.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PickupService {

    private static final Logger logger = LoggerFactory.getLogger(PickupService.class);
    private final PickupAppointmentRepository pickupAppointmentRepository;
    private final ClaimRepository claimRepository;
    private final FoundReportRepository foundReportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final SecureRandom random = new SecureRandom();

    public PickupService(
            PickupAppointmentRepository pickupAppointmentRepository,
            ClaimRepository claimRepository,
            FoundReportRepository foundReportRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            AuditService auditService) {
        this.pickupAppointmentRepository = pickupAppointmentRepository;
        this.claimRepository = claimRepository;
        this.foundReportRepository = foundReportRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public PickupDtos.PickupAppointmentResponse schedulePickup(PickupDtos.SchedulePickupRequest request, Long userId, String clientIp) {
        Claim claim = claimRepository.findById(request.getClaimId())
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));

        if (!claim.getClaimant().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("You can only schedule pickup for your own approved claim");
        }

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new IllegalStateException("Pickup can only be scheduled once claim is approved by campus staff");
        }

        // Check if appointment already exists
        PickupAppointment appointment = pickupAppointmentRepository.findByClaimId(claim.getId())
                .orElseGet(PickupAppointment::new);

        appointment.setClaim(claim);
        appointment.setScheduledDate(request.getScheduledDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setPickupLocation(request.getPickupLocation());

        if (appointment.getQrToken() == null) {
            appointment.setQrToken(UUID.randomUUID().toString().replace("-", ""));
        }
        if (appointment.getOtpCode() == null) {
            int otp = 100000 + random.nextInt(900000);
            appointment.setOtpCode(String.valueOf(otp));
        }
        appointment.setStatus("SCHEDULED");

        PickupAppointment saved = pickupAppointmentRepository.save(appointment);

        auditService.log(
                claim.getClaimant().getEmail(),
                "PICKUP_SCHEDULED",
                "PickupAppointment",
                saved.getId().toString(),
                "Pickup scheduled for " + saved.getScheduledDate() + " slot " + saved.getTimeSlot(),
                clientIp
        );

        notificationService.createNotification(
                claim.getClaimant(),
                "Pickup Appointment Confirmed",
                "Your pickup for " + claim.getFoundReport().getTitle() + " is confirmed for " + saved.getTimeSlot() + ". Present your QR code or OTP: " + saved.getOtpCode(),
                "PICKUP_READY",
                "/pickup"
        );

        return mapToAppointmentResponse(saved);
    }

    public PickupDtos.PickupAppointmentResponse getPickupByClaimId(Long claimId, Long userId) {
        PickupAppointment appointment = pickupAppointmentRepository.findByClaimId(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Pickup appointment not found for claim " + claimId));
        return mapToAppointmentResponse(appointment);
    }

    public List<PickupDtos.PickupAppointmentResponse> getPickupsForUser(Long userId) {
        return pickupAppointmentRepository.findByClaimantUserId(userId)
                .stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    public List<PickupDtos.PickupAppointmentResponse> getAllPendingPickups() {
        return pickupAppointmentRepository.findByStatusOrderByScheduledDateAsc("SCHEDULED")
                .stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PickupDtos.CollectionReceiptDto verifyAndCompletePickup(PickupDtos.VerifyPickupRequest request, Long staffId, String clientIp) {
        PickupAppointment appointment = null;

        if (request.getQrToken() != null && !request.getQrToken().isBlank()) {
            appointment = pickupAppointmentRepository.findByQrTokenForUpdate(request.getQrToken().trim())
                    .orElse(null);
        }

        if (appointment == null && request.getOtpCode() != null && !request.getOtpCode().isBlank()) {
            appointment = pickupAppointmentRepository.findByOtpCodeForUpdate(request.getOtpCode().trim())
                    .orElse(null);
        }

        if (appointment == null) {
            throw new IllegalArgumentException("Invalid QR token or OTP code. No matching appointment found.");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new IllegalStateException("Item has already been collected on " + appointment.getCollectedAt());
        }

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        appointment.setStatus("COMPLETED");
        appointment.setCollectedAt(LocalDateTime.now());
        appointment.setVerifiedByStaff(staff);

        int receiptSeq = 100000 + random.nextInt(900000);
        String receiptNumber = "RCPT-2026-" + receiptSeq;
        appointment.setReceiptNumber(receiptNumber);

        // Update Claim and FoundReport status
        Claim claim = appointment.getClaim();
        claim.setStatus(ClaimStatus.COLLECTED);
        claimRepository.save(claim);

        FoundReport foundReport = claim.getFoundReport();
        foundReport.setStatus(ReportStatus.RETURNED);
        foundReportRepository.save(foundReport);

        pickupAppointmentRepository.save(appointment);

        // Audit Trail
        auditService.log(
                staff.getEmail(),
                "PICKUP_VERIFIED_RETURNED",
                "FoundReport",
                foundReport.getReferenceId(),
                "Item collected and marked RETURNED to " + claim.getClaimant().getEmail() + " via receipt " + receiptNumber,
                clientIp
        );

        // Notification to student
        notificationService.createNotification(
                claim.getClaimant(),
                "Item Collected Successfully",
                "Your item " + foundReport.getTitle() + " (" + foundReport.getReferenceId() + ") was officially collected. Receipt: " + receiptNumber,
                "SYSTEM",
                "/pickup"
        );

        return generateReceiptDto(appointment, foundReport, claim, staff);
    }

    public PickupDtos.CollectionReceiptDto getReceipt(Long appointmentId) {
        PickupAppointment appointment = pickupAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new IllegalStateException("Pickup has not been completed yet");
        }
        return generateReceiptDto(appointment, appointment.getClaim().getFoundReport(), appointment.getClaim(), appointment.getVerifiedByStaff());
    }

    private PickupDtos.CollectionReceiptDto generateReceiptDto(PickupAppointment appointment, FoundReport foundReport, Claim claim, User staff) {
        PickupDtos.CollectionReceiptDto receipt = new PickupDtos.CollectionReceiptDto();
        receipt.setReceiptNumber(appointment.getReceiptNumber());
        receipt.setItemReferenceId(foundReport.getReferenceId());
        receipt.setItemTitle(foundReport.getTitle());
        receipt.setCategory(foundReport.getCategory());
        receipt.setClaimantName(claim.getClaimant().getFullName());
        receipt.setClaimantEmail(claim.getClaimant().getEmail());
        receipt.setClaimantStudentId(claim.getClaimant().getStudentStaffId() != null ? claim.getClaimant().getStudentStaffId() : "N/A");
        receipt.setStaffName(staff != null ? staff.getFullName() : "Campus Staff");
        receipt.setPickupLocation(appointment.getPickupLocation());
        receipt.setStorageLocation(foundReport.getStorageLocation() != null ? foundReport.getStorageLocation() : "Central Desk");
        receipt.setCollectedAt(appointment.getCollectedAt());
        receipt.setVerificationMethod("SECURE_QR_TOKEN_AND_OTP");

        // Generate SHA-256 digital signature
        String sigRaw = appointment.getReceiptNumber() + "|" + foundReport.getReferenceId() + "|" + claim.getClaimant().getEmail() + "|" + appointment.getCollectedAt();
        receipt.setDigitalSignature(sha256Hex(sigRaw));

        return receipt;
    }

    private String sha256Hex(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            return "SIG-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        }
    }

    private PickupDtos.PickupAppointmentResponse mapToAppointmentResponse(PickupAppointment a) {
        PickupDtos.PickupAppointmentResponse dto = new PickupDtos.PickupAppointmentResponse();
        dto.setId(a.getId());
        dto.setClaimId(a.getClaim().getId());
        dto.setItemTitle(a.getClaim().getFoundReport().getTitle());
        dto.setItemCategory(a.getClaim().getFoundReport().getCategory());
        dto.setItemReferenceId(a.getClaim().getFoundReport().getReferenceId());
        dto.setItemImageUrl(a.getClaim().getFoundReport().getPrimaryImageUrl());
        dto.setStorageLocation(a.getClaim().getFoundReport().getStorageLocation());
        dto.setScheduledDate(a.getScheduledDate());
        dto.setTimeSlot(a.getTimeSlot());
        dto.setPickupLocation(a.getPickupLocation());
        dto.setQrToken(a.getQrToken());
        dto.setOtpCode(a.getOtpCode());
        dto.setStatus(a.getStatus());
        dto.setReceiptNumber(a.getReceiptNumber());
        dto.setCollectedAt(a.getCollectedAt());

        // Generate Base64 QR Code using ZXing
        if (a.getQrToken() != null) {
            dto.setQrCodeBase64(generateQrCodeBase64("CAMPUSFIND-TOKEN:" + a.getQrToken(), 300, 300));
        }

        return dto;
    }

    private String generateQrCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] pngBytes = outputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngBytes);
        } catch (Exception e) {
            logger.warn("Could not render ZXing QR image: {}", e.getMessage());
            return "";
        }
    }
}
