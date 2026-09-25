package com.campusfind.repository;

import com.campusfind.entity.PickupAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PickupAppointmentRepository extends JpaRepository<PickupAppointment, Long> {

    Optional<PickupAppointment> findByClaimId(Long claimId);

    Optional<PickupAppointment> findByQrToken(String qrToken);

    Optional<PickupAppointment> findByOtpCode(String otpCode);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PickupAppointment p WHERE p.qrToken = :qrToken")
    Optional<PickupAppointment> findByQrTokenForUpdate(@Param("qrToken") String qrToken);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PickupAppointment p WHERE p.otpCode = :otpCode")
    Optional<PickupAppointment> findByOtpCodeForUpdate(@Param("otpCode") String otpCode);

    @Query("SELECT p FROM PickupAppointment p WHERE p.claim.claimant.id = :userId ORDER BY p.scheduledDate DESC")
    List<PickupAppointment> findByClaimantUserId(@Param("userId") Long userId);

    List<PickupAppointment> findByStatusOrderByScheduledDateAsc(String status);

    boolean existsByScheduledDateAndPickupLocationAndTimeSlot(LocalDateTime scheduledDate, String pickupLocation, String timeSlot);

    long countByStatus(String status);
}
