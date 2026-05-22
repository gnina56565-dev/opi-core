package ru.opi.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String specification;
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status = Status.НОВАЯ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_id")
    private Engineer engineer;

    // Связь с компетенцией (если осталась в вашей схеме)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_id")
    private Competence competence;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Поля для планирования
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private LocalDateTime slaDeadline;
    private LocalDateTime actualEnd;

    private String escalationReason;

    // Вспомогательные методы для UI (чтобы не ломать шаблон)
    public int getSlaHours() {
        return (priority != null) ? priority.getSlaHours() : 0;
    }

    public int getWaitTimeHours() {
        return (priority != null) ? priority.getWaitTimeHours() : 0;
    }

    // Для совместимости с DTO, если используются старые названия
    public String getTitle() { return this.subject; }
    public void setTitle(String title) { this.subject = title; }
    public String getDescription() { return this.specification; }
    public void setDescription(String desc) { this.specification = desc; }
}