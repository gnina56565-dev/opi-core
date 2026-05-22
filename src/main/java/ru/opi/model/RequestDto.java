package ru.opi.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequestDto {
    private Long idRequest;
    private Long idCompetence;
    private String subject;
    private String specification;
    private String contactInfo;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime slaDeadline;
    private String escalationReason;
    private LocalDateTime actualEnd;

    // Дополнительные поля для UI
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private String engineerName;

    public static RequestDto fromEntity(Request request) {
        if (request == null) return null;
        RequestDto dto = new RequestDto();
        dto.setIdRequest(request.getId());
        dto.setSubject(request.getSubject());
        dto.setSpecification(request.getSpecification());
        dto.setContactInfo(request.getContactInfo());
        dto.setPriority(request.getPriority() != null ? request.getPriority().name() : null);
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        dto.setCreatedAt(request.getCreatedAt());
        dto.setSlaDeadline(request.getSlaDeadline());
        dto.setEscalationReason(request.getEscalationReason());
        dto.setActualEnd(request.getActualEnd());
        dto.setPlannedStart(request.getPlannedStart());
        dto.setPlannedEnd(request.getPlannedEnd());

        if (request.getEngineer() != null) {
            dto.setEngineerName(request.getEngineer().getName());
        }

        if (request.getCompetence() != null) {
            dto.setIdCompetence(request.getCompetence().getId());
        }
        return dto;
    }

    public Request toEntity() {
        Request request = new Request();
        if (this.idRequest != null) {
            request.setId(this.idRequest);
        }
        // Компетенцию нужно устанавливать отдельно через сервис, здесь null
        request.setSubject(this.subject);
        request.setSpecification(this.specification);
        request.setContactInfo(this.contactInfo);
        if (this.priority != null) {
            try {
                request.setPriority(Priority.valueOf(this.priority));
            } catch (IllegalArgumentException e) {
                // Обработка неверного приоритета
            }
        }
        if (this.status != null) {
            try {
                request.setStatus(Status.valueOf(this.status));
            } catch (IllegalArgumentException e) {
                // Обработка неверного статуса
            }
        }
        request.setCreatedAt(this.createdAt);
        request.setSlaDeadline(this.slaDeadline);
        request.setEscalationReason(this.escalationReason);
        request.setActualEnd(this.actualEnd);
        return request;
    }
}