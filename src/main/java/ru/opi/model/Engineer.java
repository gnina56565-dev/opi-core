package ru.opi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "engineers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Engineer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_engineer")
    private Integer id;

    @Column(name = "fio", nullable = false, length = 150)
    private String fio;

    @Enumerated(EnumType.STRING)
    @Column(name = "line_level", nullable = false)
    private LineLevel lineLevel;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "engineer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SlaRecord> slaRecords = new ArrayList<>();

    @OneToMany(mappedBy = "engineer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Metric> metrics = new ArrayList<>();

    @OneToMany(mappedBy = "engineer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<WorkSchedule> workSchedules = new ArrayList<>();
}