package com.estevaum.akademikaapi.entities;

import com.estevaum.akademikaapi.enums.Days;
import com.estevaum.akademikaapi.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Status status;

    @Column(nullable = true)
    private Short quarter;

    @Column(nullable = true)
    private String teacher;

    @Column(nullable = true, length = 4096)
    private String syllabus;

    @ManyToMany
    @JoinTable(name = "days_subjects", joinColumns = @JoinColumn(name = "day_id"), inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Day> days;

    @OneToMany(mappedBy = "subject")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Deadline> deadlines;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Subject(User user, String name, Status status, Short quarter, String teacher, String syllabus) {
        this.user = user;
        this.name = name;
        this.status = status;
        this.quarter = quarter;
        this.teacher = teacher;
        this.syllabus = syllabus;
    }
}