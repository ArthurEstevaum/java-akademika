package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.services.DeadlineService;
import enities.Deadline; // A entidade retornada pelo Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping // Sem path base geral, definiremos nos métodos
public class DeadlineController {

    @Autowired
    private DeadlineService deadlineService;

    // Método auxiliar para obter o email do usuário autenticado (igual ao SubjectController)
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    /**
     * Endpoint para buscar todos os prazos de uma disciplina específica.
     * @param subjectId ID da disciplina no path.
     * @return Lista de Deadlines. Retorna 404 se a disciplina não for encontrada/pertencer ao usuário.
     */
    @GetMapping("/subjects/{subjectId}/deadlines") // Mapeia GET para "/subjects/{subjectId}/deadlines"
    public ResponseEntity<List<Deadline>> getDeadlinesBySubject(@PathVariable Long subjectId) {
        String userEmail = getAuthenticatedUserEmail();
        try {
            List<Deadline> deadlines = deadlineService.getDeadlinesBySubjectIdAndUserEmail(subjectId, userEmail);
            return ResponseEntity.ok(deadlines);
        } catch (RuntimeException ex) {
            // Se o SubjectService lançar exceção porque a disciplina não pertence ao usuário
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada ou acesso negado.", ex);
        }
    }

    /**
     * Endpoint para buscar um prazo específico pelo seu ID (validando a disciplina e usuário).
     * @param subjectId ID da disciplina no path.
     * @param deadlineId ID do prazo no path.
     * @return O Deadline encontrado ou 404.
     */
    @GetMapping("/subjects/{subjectId}/deadlines/{deadlineId}")
    public ResponseEntity<Deadline> getDeadlineById(@PathVariable Long subjectId, @PathVariable Long deadlineId) {
        String userEmail = getAuthenticatedUserEmail();
        return deadlineService.getDeadlineByIdAndSubjectAndUser(deadlineId, subjectId, userEmail)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

     /**
     * Endpoint para criar um novo prazo para uma disciplina.
     * @param subjectId ID da disciplina no path.
     * @param newDeadline Dados do novo prazo (ou DTO) no corpo da requisição.
     * @return O Deadline criado com status 201 Created.
     */
    @PostMapping("/subjects/{subjectId}/deadlines")
    public ResponseEntity<Deadline> createDeadline(@PathVariable Long subjectId, @RequestBody /* @Valid DeadlineCreationDTO */ Deadline newDeadline) {
         // NOTA: Usar DTO é recomendado.
        String userEmail = getAuthenticatedUserEmail();
         try {
            Deadline createdDeadline = deadlineService.createDeadline(subjectId, newDeadline, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDeadline);
        } catch (RuntimeException ex) {
             // Se o SubjectService lançar exceção porque a disciplina não pertence ao usuário
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada ou acesso negado.", ex);
        }
    }

    /**
     * Endpoint para atualizar um prazo existente (usando o ID do prazo).
     * @param deadlineId ID do prazo a atualizar.
     * @param updatedDeadline Dados atualizados (ou DTO).
     * @return O Deadline atualizado ou 404.
     */
    @PutMapping("/deadlines/{deadlineId}") // Endpoint direto para o prazo
    public ResponseEntity<Deadline> updateDeadline(@PathVariable Long deadlineId, @RequestBody /* @Valid DeadlineUpdateDTO */ Deadline updatedDeadline) {
        // NOTA: Usar DTO é recomendado.
        String userEmail = getAuthenticatedUserEmail();
        try {
            // Precisamos saber a qual disciplina este prazo pertence para validar o acesso.
            // O Service pode precisar buscar o prazo primeiro para encontrar o subjectId.
            // Assumindo que o Service handle isso internamente ou que ajustamos a assinatura do serviço:
            // Ex: O updateDeadline no service poderia retornar Optional ou lançar exceção
            // para indicar se o usuário tinha permissão.

            // Simplificação: Assumindo que o Service lança exceção se acesso for negado.
            // Pode ser necessário passar o subjectId ou o Service buscar internamente.
             Deadline deadline = deadlineService.updateDeadline(deadlineId, /* subjectId (se necessário) */ null, updatedDeadline, userEmail);
             return ResponseEntity.ok(deadline);

        } catch (RuntimeException ex) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prazo não encontrado ou acesso negado.", ex);
        }
    }

    /**
     * Endpoint para apagar um prazo.
     * @param deadlineId ID do prazo a apagar.
     * @return 204 No Content ou 404.
     */
    @DeleteMapping("/deadlines/{deadlineId}") // Endpoint direto para o prazo
    public ResponseEntity<Void> deleteDeadline(@PathVariable Long deadlineId) {
         String userEmail = getAuthenticatedUserEmail();
         try {
             // Como no PUT, o Service precisa validar a permissão do usuário
             // antes de apagar.
             deadlineService.deleteDeadline(deadlineId, /* subjectId (se necessário) */ null, userEmail);
             return ResponseEntity.noContent().build();
         } catch (RuntimeException ex) {
              throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prazo não encontrado ou acesso negado.", ex);
         }
    }
}