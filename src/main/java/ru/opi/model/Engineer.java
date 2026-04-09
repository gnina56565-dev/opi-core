package ru.opi.model;

import ru.opi.model.LineLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "engineers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Engineer {

    @Id
    @Column(name = "id_engineer")
    private Integer id;

    @Column(name = "fio", nullable = false, length = 150)
    private String fio;

    @Enumerated(EnumType.STRING)
    @Column(name = "line_level", nullable = false)
    private LineLevel lineLevel;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}