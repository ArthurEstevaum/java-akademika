package com.estevaum.akademikaapi.entities;

import com.estevaum.akademikaapi.enums.Days;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_days")
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "day_of_week")
    private Days day;

    @ManyToMany(mappedBy = "days")
    private Set<Subject> subjects;

    public Day(Days day) {
        this.day = day;
    }
}