package com.coda_fofos.java_akademika.dtos;

import com.coda_fofos.java_akademika.enums.Status; // Importar o Status
import enities.Deadline;
import enities.Subject;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO que agrega informações de resumo para o dashboard do usuário.
 */
public record DashboardSummaryDTO(
        long subjectsCursando,
        long subjectsAprovado,
        long subjectsReprovado,
        long totalDeadlines,
        List<UpcomingDeadlineDTO> upcomingDeadlines, // Lista dos próximos X prazos
        List<SubjectStatusDTO> subjectsStatus // Lista de status das matérias
) {

    /**
     * DTO simplificado para um prazo futuro.
     */
    public record UpcomingDeadlineDTO(
            Long deadlineId,
            String deadlineName,
            LocalDate date,
            Long subjectId,
            String subjectName
    ) {
        // Construtor para facilitar a conversão da entidade Deadline
        public UpcomingDeadlineDTO(Deadline deadline) {
            this(
                    deadline.getId(),
                    deadline.getName(),
                    deadline.getDate(),
                    deadline.getSubject().getId(),
                    deadline.getSubject().getName()
            );
        }
    }

    /**
     * DTO simplificado para o status de uma disciplina.
     */
    public record SubjectStatusDTO(
            Long subjectId,
            String subjectName,
            Status status
    ) {
        // Construtor para facilitar a conversão da entidade Subject
        public SubjectStatusDTO(Subject subject) {
            this(
                    subject.getId(),
                    subject.getName(),
                    subject.getStatus()
            );
        }
    }
}