package ru.opi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.opi.model.Competence;
import ru.opi.model.Engineer;

import java.math.BigDecimal;

@Entity
@Table(name = "metrics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_engineer", "id_competence"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Metric {

    @Id
    @Column(name = "id_note")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_engineer", nullable = false)
    private Engineer engineer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competence", nullable = false)
    private Competence competence;

    @Column(name = "perf_factor", nullable = false, precision = 5, scale = 2)
    private BigDecimal perfFactor; // > 0

    // Валидация на уровне бизнес-логики или через @AssertTrue (если нужна строгая проверка)
}