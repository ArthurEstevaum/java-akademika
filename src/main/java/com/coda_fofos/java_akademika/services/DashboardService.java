package com.coda_fofos.java_akademika.services;

// Importe o DTO que acabamos de definir
import com.coda_fofos.java_akademika.dtos.DashboardSummaryDTO;
import com.coda_fofos.java_akademika.enums.Status;
import com.coda_fofos.java_akademika.repositories.DeadlineRepository;
import com.coda_fofos.java_akademika.repositories.SubjectRepository;
import com.coda_fofos.java_akademika.repositories.UserRepository;
import enities.Deadline;
import enities.Subject;
import enities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService { // O nome do arquivo DEVE ser DashboardService.java

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DeadlineRepository deadlineRepository;

    private static final int UPCOMING_DAYS_LIMIT = 7;

    public DashboardSummaryDTO getDashboardSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));

        List<Subject> allSubjects = subjectRepository.findByUser(user);

        List<Deadline> allDeadlines = deadlineRepository.findBySubjectIn(allSubjects);

        long cursando = allSubjects.stream().filter(s -> s.getStatus() == Status.CURSANDO).count();
        long aprovado = allSubjects.stream().filter(s -> s.getStatus() == Status.APROVADO).count();
        long reprovado = allSubjects.stream().filter(s -> s.getStatus() == Status.REPROVADO).count();

        LocalDate today = LocalDate.now();
        LocalDate aWeekFromNow = today.plusDays(UPCOMING_DAYS_LIMIT);

        List<DashboardSummaryDTO.UpcomingDeadlineDTO> upcomingDeadlines = allDeadlines.stream()
                .filter(d -> !d.getDate().isBefore(today) && d.getDate().isBefore(aWeekFromNow))
                .sorted((d1, d2) -> d1.getDate().compareTo(d2.getDate()))
                .map(DashboardSummaryDTO.UpcomingDeadlineDTO::new)
                .collect(Collectors.toList());

        List<DashboardSummaryDTO.SubjectStatusDTO> subjectsStatus = allSubjects.stream()
                .map(DashboardSummaryDTO.SubjectStatusDTO::new)
                .collect(Collectors.toList());

        return new DashboardSummaryDTO(
                cursando,
                aprovado,
                reprovado,
                allDeadlines.size(),
                upcomingDeadlines,
                subjectsStatus
        );
    }
}