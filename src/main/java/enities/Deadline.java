package com.estevaum.akademikaapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_deadlines")
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Deadline(String name, LocalDate date, Subject subject) {
        this.name = name;
        this.date = date;
        this.subject = subject;
    }
}