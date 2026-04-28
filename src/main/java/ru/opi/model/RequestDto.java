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
}