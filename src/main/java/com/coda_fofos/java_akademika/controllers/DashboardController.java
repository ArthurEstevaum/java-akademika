package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.dtos.DashboardSummaryDTO;
import com.coda_fofos.java_akademika.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/dashboard") // Define o path base para o dashboard
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Obtém o email do usuário atualmente autenticado a partir do contexto de segurança.
     * (Este método é idêntico ao usado no SubjectController e DeadlineController)
     *
     * @return O email do usuário autenticado.
     * @throws ResponseStatusException Se não houver usuário autenticado (status 401).
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    /**
     * Endpoint para buscar o resumo de dados do dashboard do usuário autenticado.
     *
     * @return ResponseEntity com o DashboardSummaryDTO e status 200 OK.
     */
    @GetMapping // Mapeia GET para "/dashboard"
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        String userEmail = getAuthenticatedUserEmail();

        // O DashboardService faz todo o trabalho de agregação
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userEmail);

        return ResponseEntity.ok(summary);
    }
}