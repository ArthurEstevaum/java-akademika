package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.dtos.DashboardSummaryDTO;
import com.coda_fofos.java_akademika.enums.Status;
import com.coda_fofos.java_akademika.repositories.DeadlineRepository;
import com.coda_fofos.java_akademika.repositories.SubjectRepository;
import com.coda_fofos.java_akademika.repositories.UserRepository;
import enities.Deadline;
import enities.Subject;
import enities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private DeadlineRepository deadlineRepository;

    @InjectMocks // Cria a instância do DashboardService injetando os Mocks acima
    private DashboardService dashboardService;

    private User testUser;
    private List<Subject> testSubjects;
    private List<Deadline> testDeadlines;
    private final String userEmail = "test@user.com";

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "pass123", userEmail);

        // --- Setup de Disciplinas ---
        Subject s1 = new Subject(testUser, "POO", Status.CURSANDO, (short) 2, "Prof A", "Ementa A");
        s1.setId(1L);
        Subject s2 = new Subject(testUser, "Banco de Dados", Status.CURSANDO, (short) 2, "Prof B", "Ementa B");
        s2.setId(2L);
        Subject s3 = new Subject(testUser, "Logica", Status.APROVADO, (short) 1, "Prof C", "Ementa C");
        s3.setId(3L);
        Subject s4 = new Subject(testUser, "Calculo", Status.REPROVADO, (short) 1, "Prof D", "Ementa D");
        s4.setId(4L);

        testSubjects = List.of(s1, s2, s3, s4);

        // --- Setup de Prazos (Deadlines) ---
        LocalDate today = LocalDate.now();

        // Prazos que DEVEM aparecer no resumo "upcoming"
        Deadline d1_upcoming = new Deadline("Prova 1", today.plusDays(3), s1); // Daqui 3 dias
        Deadline d2_upcoming = new Deadline("Trabalho BD", today.plusDays(7), s2); // Daqui 7 dias (limite)

        // Prazos que NÃO DEVEM aparecer no resumo "upcoming"
        Deadline d3_past = new Deadline("Lista Antiga", today.minusDays(5), s1); // Passado
        Deadline d4_future = new Deadline("Trabalho Final", today.plusDays(10), s2); // Futuro distante

        testDeadlines = List.of(d1_upcoming, d2_upcoming, d3_past, d4_future);
    }

    @Test
    @DisplayName("Deve retornar o sumário completo do dashboard com sucesso")
    void getDashboardSummary_Success() {
        // Arrange
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findByUser(testUser)).thenReturn(testSubjects);
        when(deadlineRepository.findBySubjectIn(testSubjects)).thenReturn(testDeadlines);

        // Act
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userEmail);

        // Assert
        assertNotNull(summary);

        // 1. Contagem de Status
        assertEquals(2, summary.subjectsCursando());
        assertEquals(1, summary.subjectsAprovado());
        assertEquals(1, summary.subjectsReprovado());

        // 2. Contagem total de Prazos
        assertEquals(4, summary.totalDeadlines());

        // 3. Verificação de Prazos Futuros (Próximos 7 dias)
        assertNotNull(summary.upcomingDeadlines());
        assertEquals(2, summary.upcomingDeadlines().size());
        // Verifica a ordem (o de 3 dias deve vir antes do de 7 dias)
        assertEquals("Prova 1", summary.upcomingDeadlines().get(0).deadlineName());
        assertEquals("Trabalho BD", summary.upcomingDeadlines().get(1).deadlineName());

        // 4. Verificação da Lista de Status
        assertNotNull(summary.subjectsStatus());
        assertEquals(4, summary.subjectsStatus().size());

        // 5. Verifica se os repositórios foram chamados
        verify(userRepository).findByEmail(userEmail);
        verify(subjectRepository).findByUser(testUser);
        verify(deadlineRepository).findBySubjectIn(testSubjects);
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário não for encontrado")
    void getDashboardSummary_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dashboardService.getDashboardSummary("nouser@mail.com");
        });

        assertEquals("Usuário não encontrado: nouser@mail.com", exception.getMessage());

        // Garante que os outros repositórios não foram chamados
        verify(subjectRepository, never()).findByUser(any());
        verify(deadlineRepository, never()).findBySubjectIn(any());
    }

    @Test
    @DisplayName("Deve retornar um sumário vazio se o usuário não tiver disciplinas")
    void getDashboardSummary_NoSubjects() {
        // Arrange
        List<Subject> noSubjects = Collections.emptyList();
        List<Deadline> noDeadlines = Collections.emptyList();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findByUser(testUser)).thenReturn(noSubjects);
        when(deadlineRepository.findBySubjectIn(noSubjects)).thenReturn(noDeadlines);

        // Act
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userEmail);

        // Assert
        assertNotNull(summary);
        assertEquals(0, summary.subjectsCursando());
        assertEquals(0, summary.subjectsAprovado());
        assertEquals(0, summary.subjectsReprovado());
        assertEquals(0, summary.totalDeadlines());
        assertTrue(summary.upcomingDeadlines().isEmpty());
        assertTrue(summary.subjectsStatus().isEmpty());
    }
}