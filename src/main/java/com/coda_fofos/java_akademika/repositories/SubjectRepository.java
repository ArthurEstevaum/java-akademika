package com.coda_fofos.java_akademika.repositories;

import enities.Subject; // Importa a entidade Subject
import enities.User;   // Importa a entidade User (se precisar buscar Subjects por User)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface Repository para a entidade Subject.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> { // <Subject, Long>

    /**
     * Método customizado para buscar todas as disciplinas associadas a um usuário específico.
     * O Spring Data JPA implementa a query buscando pelo campo 'user' na entidade Subject.
     *
     * @param user O objeto User cujas disciplinas devem ser buscadas.
     * @return Uma lista de Subjects pertencentes ao usuário fornecido.
     */
    List<Subject> findByUser(User user);

    List<Subject> findAllByUserEmail(String userEmail);

    List<Subject> findByIdAndUserEmail(Long id, String userEmail);

    Optional<Subject> getByIdAndUserEmail(Long id, String userEmail);

    // Exemplo: Buscar disciplinas de um usuário por ID do usuário
    // List<Subject> findByUserId(Long userId);

    // Outros métodos podem ser adicionados conforme a necessidade, como:
    // List<Subject> findByUserAndNameContainingIgnoreCase(User user, String name);
}