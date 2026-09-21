package com.campusfind.service;

import com.campusfind.entity.AuditLog;
import com.campusfind.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String actorEmail, String action, String entityType, String entityId, String details, String ipAddress) {
        AuditLog auditLog = new AuditLog(actorEmail, action, entityType, entityId, details, ipAddress);
        auditLogRepository.save(auditLog);
    }
}
