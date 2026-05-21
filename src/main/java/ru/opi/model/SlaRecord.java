package ru.opi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_request"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_note")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_engineer", nullable = false, foreignKey = @ForeignKey(name = "fk_sla_engineer"))
    @JsonIgnore
    private Engineer engineer;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_request", nullable = false)
    private Request request;

    @Column(name = "planned_start", nullable = false)
    private LocalDateTime plannedStart;

    @Column(name = "planned_end", nullable = false)
    private LocalDateTime plannedEnd;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Column(name = "sla_forecast", nullable = false)
    private Boolean slaForecast;
}