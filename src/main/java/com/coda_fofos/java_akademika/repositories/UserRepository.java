package com.coda_fofos.java_akademika.repositories;

import enities.User; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository 
public interface UserRepository extends JpaRepository<User, Long> { 
    /**
     * Método customizado para buscar um usuário pelo seu endereço de email.
     * O Spring Data JPA implementará automaticamente a query SQL correspondente
     * com base no nome do método ("findByEmail").
     *
     * @param email O email a ser buscado.
     * @return Um Optional contendo o User se encontrado, ou um Optional vazio caso contrário.
     * Usar Optional ajuda a evitar NullPointerExceptions.
     */
    Optional<User> findByEmail(String email);

    // Você pode adicionar outros métodos de consulta aqui se necessário, por exemplo:
    // Optional<User> findByUsername(String username);
    // boolean existsByEmail(String email);
}