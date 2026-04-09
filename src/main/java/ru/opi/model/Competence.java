package ru.opi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "competences")
public class Competence {
    @Id
    @Column(name = "id_competence")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "specification")
    private String specification;

    public Competence() {}

    public Competence(Integer id, String name, String specification) {
        this.id = id;
        this.name = name;
        this.specification = specification;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}