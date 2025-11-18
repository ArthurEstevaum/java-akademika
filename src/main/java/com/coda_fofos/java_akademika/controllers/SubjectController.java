package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.dtos.subject.DeadlineDTO;
import com.coda_fofos.java_akademika.dtos.subject.SubjectCreationDTO;
import com.coda_fofos.java_akademika.dtos.subject.SubjectResponseDTO;
import com.coda_fofos.java_akademika.dtos.subject.SubjectUpdateDTO;
import com.coda_fofos.java_akademika.services.SubjectService;
import enities.Day;
import enities.Subject; // A entidade retornada pelo Service
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Para obter o usuário autenticado
import org.springframework.security.core.context.SecurityContextHolder; // Para obter o usuário autenticado
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Para lançar erros HTTP

import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/subjects")
public class SubjectController {
    private final SubjectService subjectService;

    private final Function<Subject, SubjectResponseDTO>  subjectToResponseDTO = subject -> {

        return new SubjectResponseDTO(subject.getId(),
                subject.getName(), subject.getQuarter(), subject.getStatus(),
                subject.getSyllabus(), subject.getTeacher(),
                subject.getDays().stream().map(Day::getDay).toList(),
                subject.getDeadlines() == null? List.of() : subject.getDeadlines().stream().map(deadline -> new DeadlineDTO(deadline.getDate(), deadline.getName())).toList()
        );
    };

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/")
    public ResponseEntity<List<SubjectResponseDTO>> getAllUserSubjects() {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Subject> userSubjects = subjectService.getAllUserSubjects(authenticatedUserEmail);
        List <SubjectResponseDTO> userSubjectsResponse = userSubjects.stream().map(subjectToResponseDTO).toList();

        return ResponseEntity.ok(userSubjectsResponse);
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectResponseDTO> getUserSubject(@PathVariable Long subjectId) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Subject userSubject = subjectService.getUserSubject(subjectId, authenticatedUserEmail);
        SubjectResponseDTO subjectResponse = subjectToResponseDTO.apply(userSubject);

        return ResponseEntity.ok(subjectResponse);
    }

    @PostMapping("/")
    public ResponseEntity<SubjectResponseDTO> createSubject(@Valid @RequestBody SubjectCreationDTO requestData) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Subject subject = subjectService.createSubject(requestData, authenticatedUserEmail);
        SubjectResponseDTO subjectResponse = subjectToResponseDTO.apply(subject);

        return ResponseEntity.status(201).body(subjectResponse);
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectResponseDTO> updateSubject(@Valid @RequestBody SubjectUpdateDTO requestData, @PathVariable Long subjectId) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Subject subject = subjectService.updateUserSubject(subjectId, authenticatedUserEmail, requestData);
        SubjectResponseDTO subjectResponse = subjectToResponseDTO.apply(subject);

        return ResponseEntity.ok(subjectResponse);
    }

    @DeleteMapping("/{subjectId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long subjectId) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        subjectService.deleteSubject(subjectId, authenticatedUserEmail);

        return ResponseEntity.ok("Disciplina excluída com sucesso.");
    }
}