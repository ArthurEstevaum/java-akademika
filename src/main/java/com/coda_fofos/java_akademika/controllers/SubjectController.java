package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.services.SubjectService;
import enities.Subject; // A entidade retornada pelo Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Para obter o usuário autenticado
import org.springframework.security.core.context.SecurityContextHolder; // Para obter o usuário autenticado
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Para lançar erros HTTP

import java.util.List;

@RestController
@RequestMapping("/subjects") // Path base para disciplinas
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * Obtém o email do usuário atualmente autenticado a partir do contexto de segurança.
     * @return O email do usuário autenticado.
     * @throws ResponseStatusException Se não houver usuário autenticado (status 401).
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        // Assumindo que o 'name' ou 'principal' da autenticação é o email (comum com JWT/OAuth2)
        return authentication.getName();
    }

    /**
     * Endpoint para buscar todas τις disciplinas do usuário autenticado.
     * @return ResponseEntity com a lista de Subjects e status 200 OK.
     */
    @GetMapping // Mapeia GET para "/subjects"
    public ResponseEntity<List<Subject>> getAllSubjects() {
        String userEmail = getAuthenticatedUserEmail();
        List<Subject> subjects = subjectService.getSubjectsByUserEmail(userEmail);
        return ResponseEntity.ok(subjects);
    }

    /**
     * Endpoint para buscar uma disciplina específica pelo ID.
     * @param id O ID da disciplina passado no path (ex: /subjects/123).
     * @return ResponseEntity com a Subject encontrada e status 200 OK, ou 404 Not Found.
     */
    @GetMapping("/{id}") // Mapeia GET para "/subjects/{id}"
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        String userEmail = getAuthenticatedUserEmail();
        return subjectService.getSubjectByIdAndUserEmail(id, userEmail)
                .map(ResponseEntity::ok) // Se encontrar, retorna 200 OK com o subject
                .orElseGet(() -> ResponseEntity.notFound().build()); // Se não encontrar, retorna 404 Not Found
    }

    /**
     * Endpoint para criar uma nova disciplina.
     * @param newSubject Objeto Subject (ou um DTO específico para criação) vindo do corpo da requisição.
     * @return ResponseEntity com a Subject criada e status 201 Created.
     */
    @PostMapping // Mapeia POST para "/subjects"
    public ResponseEntity<Subject> createSubject(@RequestBody /* @Valid SubjectCreationDTO */ Subject newSubject) {
        // NOTA: Idealmente, receberia um DTO (SubjectCreationDTO) e o Service o converteria para Subject.
        // O @Valid validaria o DTO. Por simplicidade, estamos recebendo a entidade diretamente por enquanto.
        String userEmail = getAuthenticatedUserEmail();
        Subject createdSubject = subjectService.createSubject(newSubject, userEmail);
        // Idealmente, retornaria o URI do novo recurso no cabeçalho Location, mas omitido por simplicidade.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
    }

    /**
     * Endpoint para atualizar uma disciplina existente.
     * @param id O ID da disciplina a atualizar.
     * @param updatedSubject Objeto Subject (ou um DTO específico para atualização) com os novos dados.
     * @return ResponseEntity com a Subject atualizada e status 200 OK, ou 404 Not Found.
     */
    @PutMapping("/{id}") // Mapeia PUT para "/subjects/{id}"
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody /* @Valid SubjectUpdateDTO */ Subject updatedSubject) {
        // NOTA: Como no POST, idealmente receberia um DTO (SubjectUpdateDTO).
        String userEmail = getAuthenticatedUserEmail();
        try {
            Subject subject = subjectService.updateSubject(id, updatedSubject, userEmail);
            return ResponseEntity.ok(subject);
        } catch (RuntimeException ex) { // Captura a exceção lançada pelo Service se não encontrar
             // Idealmente, tratar exceções específicas (ex: SubjectNotFoundException)
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada ou não pertence ao usuário.", ex);
        }
    }

    /**
     * Endpoint para apagar uma disciplina.
     * @param id O ID da disciplina a apagar.
     * @return ResponseEntity com status 204 No Content em caso de sucesso, ou 404 Not Found.
     */
    @DeleteMapping("/{id}") // Mapeia DELETE para "/subjects/{id}"
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        String userEmail = getAuthenticatedUserEmail();
        try {
            subjectService.deleteSubject(id, userEmail);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (RuntimeException ex) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada ou não pertence ao usuário.", ex);
        }
    }
}