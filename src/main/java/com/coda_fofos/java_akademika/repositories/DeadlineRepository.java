package com.coda_fofos.java_akademika.repositories;

import enities.Deadline;
import enities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeadlineRepository extends JpaRepository<Deadline, Long> {

    /**
     * Busca todos os prazos associados a uma disciplina específica.
     */
    List<Deadline> findBySubject(Subject subject);

    /**
     * !! ADICIONE ESTE MÉTODO !!
     * Busca todos os prazos pertencentes a uma lista de disciplinas.
     * Usado pelo DashboardService para pegar todos os prazos de um usuário de uma vez.
     *
     * @param subjects A lista de disciplinas do usuário.
     * @return Uma lista de todos os Deadlines associados a essas disciplinas.
     */
    List<Deadline> findBySubjectIn(List<Subject> subjects);
}