package com.campusfind.service;

import com.campusfind.entity.*;
import com.campusfind.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerService.class);

    private final UserRepository userRepository;
    private final CampusLocationRepository campusLocationRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final FoundReportRepository foundReportRepository;
    private final LostReportRepository lostReportRepository;
    private final ClaimRepository claimRepository;
    private final PickupAppointmentRepository pickupAppointmentRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AiMatchingService aiMatchingService;

    public DataInitializerService(
            UserRepository userRepository,
            CampusLocationRepository campusLocationRepository,
            ItemCategoryRepository itemCategoryRepository,
            FoundReportRepository foundReportRepository,
            LostReportRepository lostReportRepository,
            ClaimRepository claimRepository,
            PickupAppointmentRepository pickupAppointmentRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder,
            AiMatchingService aiMatchingService) {
        this.userRepository = userRepository;
        this.campusLocationRepository = campusLocationRepository;
        this.itemCategoryRepository = itemCategoryRepository;
        this.foundReportRepository = foundReportRepository;
        this.lostReportRepository = lostReportRepository;
        this.claimRepository = claimRepository;
        this.pickupAppointmentRepository = pickupAppointmentRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.aiMatchingService = aiMatchingService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already initialized with campus data.");
            return;
        }

        logger.info("Initializing CampusFind database with categories, locations, and demo users...");

        // 1. Categories
        List<ItemCategory> categories = Arrays.asList(
                new ItemCategory("Bags", "briefcase", "Backpacks, tote bags, duffle bags, pouches"),
                new ItemCategory("Electronics", "laptop", "Laptops, chargers, headphones, calculators, USB drives"),
                new ItemCategory("Phones", "smartphone", "Smartphones, tablets, smart watches"),
                new ItemCategory("Wallets", "credit-card", "Wallets, coin purses, card holders"),
                new ItemCategory("ID Cards", "badge-check", "Student identity cards, access badges, driving licenses"),
                new ItemCategory("Bottles", "cup-soda", "Water bottles, thermos flasks, tumblers"),
                new ItemCategory("Keys", "key", "Hostel room keys, vehicle keys, padlock keys"),
                new ItemCategory("Umbrellas", "umbrella", "Rain umbrellas, foldable umbrellas"),
                new ItemCategory("Books", "book", "Textbooks, notebooks, binders, study material"),
                new ItemCategory("Clothing", "shirt", "Jackets, hoodies, caps, scarves"),
                new ItemCategory("Other", "help-circle", "Miscellaneous campus personal items")
        );
        itemCategoryRepository.saveAll(categories);

        // 2. Campus Locations
        CampusLocation library = new CampusLocation("Central Library", "North Quad", "4-storey central academic library with study carrels", 4, "Notebooks, chargers, water bottles, bags", 12.8231, 80.0452);
        CampusLocation cafeteria = new CampusLocation("Student Cafeteria", "Central Hub", "Main university dining court & food stalls", 2, "Wallets, phones, umbrellas, cards", 12.8239, 80.0461);
        CampusLocation blockA = new CampusLocation("Academic Block A", "Lecture Zone", "Undergraduate engineering classrooms and seminar halls", 6, "ID cards, textbooks, pencil cases", 12.8245, 80.0440);
        CampusLocation csLab = new CampusLocation("Computer Science Lab Block", "Tech Quad", "High-performance labs and server rooms", 4, "Laptops, pendrives, mouse, headphones", 12.8250, 80.0435);
        CampusLocation sports = new CampusLocation("Sports Complex", "South Zone", "Indoor stadium, gym, basketball and tennis courts", 2, "Bottles, fitness bands, locker keys", 12.8210, 80.0470);
        CampusLocation hostel = new CampusLocation("Hostel Block A", "Residential Quad", "Men & women dormitories and study lounges", 8, "Room keys, laundry items, footwear", 12.8260, 80.0480);
        CampusLocation mainGate = new CampusLocation("Campus Main Gate", "Transit Zone", "Main university entrance, security booth & shuttle stop", 1, "Umbrellas, transit passes, bus cards", 12.8200, 80.0430);
        CampusLocation auditorium = new CampusLocation("University Auditorium", "Cultural Zone", "1500-seat main campus convocation hall", 3, "Programs, jackets, spectacles", 12.8225, 80.0445);

        campusLocationRepository.saveAll(Arrays.asList(library, cafeteria, blockA, csLab, sports, hostel, mainGate, auditorium));

        // 3. Demo Users
        User student = new User("student@campus.edu", passwordEncoder.encode("Password123!"), "Alex Chen", Role.ROLE_STUDENT, "STU-2024-8841", "+1 555-0192", "Computer Science");
        User staff = new User("staff@campus.edu", passwordEncoder.encode("Password123!"), "Sarah Jenkins", Role.ROLE_STAFF, "STF-1022", "+1 555-0144", "Lost & Found Central Office");
        User admin = new User("admin@campus.edu", passwordEncoder.encode("Password123!"), "Campus Administrator", Role.ROLE_ADMIN, "ADM-001", "+1 555-0100", "Campus Safety & Administration");
        User student2 = new User("priya@campus.edu", passwordEncoder.encode("Password123!"), "Priya Sharma", Role.ROLE_STUDENT, "STU-2024-9102", "+1 555-0188", "Electrical Engineering");

        userRepository.saveAll(Arrays.asList(student, staff, admin, student2));

        // 4. Seed Found Reports
        FoundReport backpackFound = new FoundReport();
        backpackFound.setReferenceId("LF-2026-102341");
        backpackFound.setFinder(student2);
        backpackFound.setTitle("Black Nike Backpack with Red Zipper Pull");
        backpackFound.setCategory("Bags");
        backpackFound.setColor("Black");
        backpackFound.setBrand("Nike");
        backpackFound.setMaterial("Durable Nylon Fabric");
        backpackFound.setDistinctiveFeatures("Small red keychain attached to side zip, padded laptop sleeve");
        backpackFound.setVisibleText("Nike Swoosh emblem");
        backpackFound.setPublicDescription("Found a black Nike backpack on the 2nd floor quiet study area of Central Library.");
        backpackFound.setPrivateVerificationDetails("Contains blue spiral notebook with 'Data Structures' on cover, 65W Dell charger, and a pack of mints.");
        backpackFound.setCampusLocation(library);
        backpackFound.setSpecificArea("2nd Floor Study Cubicles");
        backpackFound.setFoundDate(LocalDateTime.now().minusHours(18));
        backpackFound.setPossessionStatus(PossessionStatus.STORED_IN_OFFICE);
        backpackFound.setStatus(ReportStatus.VERIFIED);
        backpackFound.setStorageLocation("Office Room 102 - Shelf S-12");
        backpackFound.setPrimaryImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80");
        backpackFound.setThumbnailImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&q=80");
        backpackFound.setAiConfidence(0.95);

        FoundReport bottleFound = new FoundReport();
        bottleFound.setReferenceId("LF-2026-102342");
        bottleFound.setFinder(student);
        bottleFound.setTitle("Blue Insulated Hydro Flask 32oz");
        bottleFound.setCategory("Bottles");
        bottleFound.setColor("Blue / Navy");
        bottleFound.setBrand("Hydro Flask");
        bottleFound.setMaterial("Stainless Steel");
        bottleFound.setDistinctiveFeatures("A sticker of GitHub Octocat and slight dent on lower base");
        bottleFound.setVisibleText("Hydro Flask");
        bottleFound.setPublicDescription("Blue metal insulated water bottle left behind in the basketball court seating area.");
        bottleFound.setPrivateVerificationDetails("Has custom black silicone flex boot on the base and small 'SRM' engraving near neck.");
        bottleFound.setCampusLocation(sports);
        bottleFound.setSpecificArea("Indoor Basketball Court Row 3");
        bottleFound.setFoundDate(LocalDateTime.now().minusHours(36));
        bottleFound.setPossessionStatus(PossessionStatus.STORED_IN_OFFICE);
        bottleFound.setStatus(ReportStatus.VERIFIED);
        bottleFound.setStorageLocation("Office Room 102 - Locker 4");
        bottleFound.setPrimaryImageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800&q=80");
        bottleFound.setThumbnailImageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400&q=80");
        bottleFound.setAiConfidence(0.93);

        FoundReport walletFound = new FoundReport();
        walletFound.setReferenceId("LF-2026-102343");
        walletFound.setFinder(staff);
        walletFound.setTitle("Brown Leather Bi-Fold Wallet");
        walletFound.setCategory("Wallets");
        walletFound.setColor("Brown / Tan");
        walletFound.setBrand("Fossil");
        walletFound.setMaterial("Genuine Leather");
        walletFound.setDistinctiveFeatures("Distressed vintage finish with magnetic coin pouch");
        walletFound.setVisibleText("Fossil embossed");
        walletFound.setPublicDescription("Brown leather wallet found near table 14 in the main student cafeteria.");
        walletFound.setPrivateVerificationDetails("Contains transit card, gym locker card #48, and three ₹100 currency notes.");
        walletFound.setCampusLocation(cafeteria);
        walletFound.setSpecificArea("Dining Court Table 14");
        walletFound.setFoundDate(LocalDateTime.now().minusHours(8));
        walletFound.setPossessionStatus(PossessionStatus.STORED_IN_OFFICE);
        walletFound.setStatus(ReportStatus.VERIFIED);
        walletFound.setStorageLocation("Office Safe Box - Compartment B");
        walletFound.setPrimaryImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?w=800&q=80");
        walletFound.setThumbnailImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?w=400&q=80");
        walletFound.setAiConfidence(0.91);

        FoundReport idCardFound = new FoundReport();
        idCardFound.setReferenceId("LF-2026-102344");
        idCardFound.setFinder(staff);
        idCardFound.setTitle("Student ID Badge with Blue Lanyard");
        idCardFound.setCategory("ID Cards");
        idCardFound.setColor("Blue / Navy");
        idCardFound.setBrand("Campus Services");
        idCardFound.setMaterial("Laminated Card");
        idCardFound.setDistinctiveFeatures("Official university lanyard with quick-release buckle");
        idCardFound.setVisibleText("STU-****-8841 [Masked for Privacy]");
        idCardFound.setPublicDescription("Student ID card found near turnstiles of Academic Block A.");
        idCardFound.setPrivateVerificationDetails("Student name Alex Chen, department Computer Science, year 2024.");
        idCardFound.setCampusLocation(blockA);
        idCardFound.setSpecificArea("Turnstiles West Gate");
        idCardFound.setFoundDate(LocalDateTime.now().minusHours(4));
        idCardFound.setPossessionStatus(PossessionStatus.STORED_IN_OFFICE);
        idCardFound.setStatus(ReportStatus.READY_FOR_PICKUP);
        idCardFound.setStorageLocation("ID Card Holder Tray #1");
        idCardFound.setPrimaryImageUrl("https://images.unsplash.com/photo-1578852612716-854e527abf50?w=800&q=80");
        idCardFound.setThumbnailImageUrl("https://images.unsplash.com/photo-1578852612716-854e527abf50?w=400&q=80");
        idCardFound.setAiConfidence(0.98);

        foundReportRepository.saveAll(Arrays.asList(backpackFound, bottleFound, walletFound, idCardFound));

        // 5. Seed Lost Reports
        LostReport backpackLost = new LostReport();
        backpackLost.setReferenceId("LR-2026-204101");
        backpackLost.setOwner(student);
        backpackLost.setTitle("Black Nike Backpack with Red Zipper Pull");
        backpackLost.setCategory("Bags");
        backpackLost.setColor("Black");
        backpackLost.setBrand("Nike");
        backpackLost.setDistinctiveFeatures("Red keychain pull, front pouch has Dell laptop charger and Data Structures notes");
        backpackLost.setDescription("I was studying at Central Library 2nd floor yesterday evening and mistakenly walked away without my black Nike backpack.");
        backpackLost.setCampusLocation(library);
        backpackLost.setSpecificArea("2nd Floor Cubicles");
        backpackLost.setLostDate(LocalDateTime.now().minusHours(20));
        backpackLost.setStatus("ACTIVE");
        backpackLost.setContactPreference("In-App");
        backpackLost.setReferenceImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80");

        LostReport idCardLost = new LostReport();
        idCardLost.setReferenceId("LR-2026-204102");
        idCardLost.setOwner(student);
        idCardLost.setTitle("Student ID Badge on Blue Lanyard");
        idCardLost.setCategory("ID Cards");
        idCardLost.setColor("Blue / Navy");
        idCardLost.setBrand("Campus Services");
        idCardLost.setDistinctiveFeatures("Has my photo and CSE dept tag");
        idCardLost.setDescription("Misplaced my student identity card somewhere between Academic Block A and the library.");
        idCardLost.setCampusLocation(blockA);
        idCardLost.setSpecificArea("Near main entrance turnstiles");
        idCardLost.setLostDate(LocalDateTime.now().minusHours(6));
        idCardLost.setStatus("MATCHED");
        idCardLost.setContactPreference("In-App");

        lostReportRepository.saveAll(Arrays.asList(backpackLost, idCardLost));

        // 6. Compute Matches between Seed Items
        aiMatchingService.matchLostReport(backpackLost);
        aiMatchingService.matchLostReport(idCardLost);

        // 7. Seed an approved Claim & Pickup Appointment for demonstration
        Claim approvedClaim = new Claim();
        approvedClaim.setFoundReport(idCardFound);
        approvedClaim.setClaimant(student);
        approvedClaim.setLostReport(idCardLost);
        approvedClaim.setClaimantAnswers("Alex Chen, Computer Science 2024, ID STU-2024-8841");
        approvedClaim.setConsistencyScore("HIGH");
        approvedClaim.setConsistencyAnalysis("Perfect match: student name and ID match masked verification PVC details.");
        approvedClaim.setStatus(ClaimStatus.APPROVED);
        approvedClaim.setAdminNotes("Verified with university registry. Ready for collection.");
        approvedClaim.setReviewedBy(staff);
        approvedClaim.setReviewedAt(LocalDateTime.now().minusHours(2));
        claimRepository.save(approvedClaim);

        PickupAppointment appointment = new PickupAppointment();
        appointment.setClaim(approvedClaim);
        appointment.setScheduledDate(LocalDateTime.now().plusHours(4));
        appointment.setTimeSlot("02:00 - 02:30 PM");
        appointment.setPickupLocation("Campus Lost & Found Office - Student Center Room 102");
        appointment.setQrToken("cf74891bcae4492987ff632b84291456");
        appointment.setOtpCode("482731");
        appointment.setStatus("SCHEDULED");
        pickupAppointmentRepository.save(appointment);

        // Seed initial notifications for student
        Notification n1 = new Notification(
                student,
                "Strong Potential Match Detected (96%)",
                "Your lost 'Black Nike Backpack' matches found report LF-2026-102341 at Central Library.",
                "MATCH_FOUND",
                "/matches"
        );
        Notification n2 = new Notification(
                student,
                "Pickup Scheduled: ID Card",
                "Your pickup for Student ID Badge is confirmed for today at 02:00 PM. Pickup OTP: 482731",
                "PICKUP_READY",
                "/pickup"
        );
        notificationRepository.saveAll(Arrays.asList(n1, n2));

        logger.info("CampusFind demo data successfully loaded.");
    }
}
