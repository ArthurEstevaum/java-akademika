package com.coda_fofos.java_akademika.repositories;

import com.coda_fofos.java_akademika.enums.Days; // Importa o Enum Days
import enities.Day; // Importa a entidade Day
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface Repository para a entidade Day.
 */
@Repository
public interface DayRepository extends JpaRepository<Day, Long> { // <Day, Long>

    /**
     * Método customizado para buscar uma entidade Day pelo valor do Enum Days.
     *
     * @param dayEnum O valor do enum Days (ex: Days.SEGUNDA).
     * @return Um Optional contendo a entidade Day correspondente, se existir.
     */
    Optional<Day> findByDay(Days dayEnum);

}