package com.coda_fofos.java_akademika.repositories;

import enities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    public Optional<Activity> findByName(String name);
}
