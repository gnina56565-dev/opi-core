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

    public static RequestDto fromEntity(Request request) {
        if (request == null) return null;
        RequestDto dto = new RequestDto();
        dto.setIdRequest(request.getId() != null ? request.getId().longValue() : null);
        dto.setSubject(request.getSubject());
        dto.setSpecification(request.getSpecification());
        dto.setContactInfo(request.getContactInfo());
        dto.setPriority(request.getPriority() != null ? request.getPriority().name() : null);
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        dto.setCreatedAt(request.getCreatedAt());
        dto.setSlaDeadline(request.getSlaDeadline());
        dto.setEscalationReason(request.getEscalationReason());
        dto.setActualEnd(request.getActualEnd());
        if (request.getCompetence() != null) {
            dto.setIdCompetence(request.getCompetence().getId() != null ? request.getCompetence().getId().longValue() : null);
        }
        return dto;
    }

    public Request toEntity(Competence competence) {
        Request request = new Request();
        if (this.idRequest != null) {
            request.setId(this.idRequest.intValue());
        }
        request.setCompetence(competence);
        request.setSubject(this.subject);
        request.setSpecification(this.specification);
        request.setContactInfo(this.contactInfo);
        if (this.priority != null) {
            request.setPriority(Priority.valueOf(this.priority));
        }
        if (this.status != null) {
            request.setStatus(Status.valueOf(this.status));
        }
        request.setCreatedAt(this.createdAt);
        request.setSlaDeadline(this.slaDeadline);
        request.setEscalationReason(this.escalationReason);
        request.setActualEnd(this.actualEnd);
        return request;
    }

    public void updateEntity(Request request, Competence competence) {
        if (this.subject != null) request.setSubject(this.subject);
        if (this.specification != null) request.setSpecification(this.specification);
        if (this.contactInfo != null) request.setContactInfo(this.contactInfo);
        if (this.priority != null) request.setPriority(Priority.valueOf(this.priority));
        if (this.status != null) request.setStatus(Status.valueOf(this.status));
        if (this.escalationReason != null) request.setEscalationReason(this.escalationReason);
        if (this.actualEnd != null) request.setActualEnd(this.actualEnd);
        if (competence != null) request.setCompetence(competence);
    }
}