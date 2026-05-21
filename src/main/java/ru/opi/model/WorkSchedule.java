package ru.opi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.opi.model.Engineer;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_engineer", "date"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_schedule")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_engineer", nullable = false, foreignKey = @ForeignKey(name = "fk_workschedule_engineer"))
    @JsonIgnore
    private Engineer engineer;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "break_start")
    private LocalTime breakStart;

    @Column(name = "break_end")
    private LocalTime breakEnd;
}