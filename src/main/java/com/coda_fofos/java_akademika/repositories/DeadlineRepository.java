package com.coda_fofos.java_akademika.repositories;

import enities.Deadline; 
import enities.Subject; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface DeadlineRepository extends JpaRepository<Deadline, Long> { // <Deadline, Long>

    /**
     * Método customizado para buscar todos os prazos associados a uma disciplina específica.
     *
     * @param subject A disciplina cujos prazos devem ser buscados.
     * @return Uma lista de Deadlines pertencentes à disciplina fornecida.
     */
    List<Deadline> findBySubject(Subject subject);


}