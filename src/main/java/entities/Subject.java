package com.coda_fofos.java_akademika.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String professor;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Subject(String name, String professor, User user) {
        this.name = name;
        this.professor = professor;
        this.user = user;
    }
}
