package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.repositories.DeadlineRepository;
import enities.Deadline;
import enities.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeadlineServiceTest {

    @Mock
    private DeadlineRepository deadlineRepository;

    @Mock
    private SubjectService subjectService; 

    @InjectMocks
    private DeadlineService deadlineService;

    private Subject testSubject;
    private Deadline testDeadline;
    private final Long subjectId = 1L;
    private final Long deadlineId = 10L;
    private final String userEmail = "user@test.com";

    @BeforeEach
    void setUp() {
        testSubject = new Subject();
        testSubject.setId(subjectId);
        testSubject.setName("Ciência da Computação");

        testDeadline = new Deadline();
        testDeadline.setId(deadlineId);
        testDeadline.setName("Entrega T1");
        testDeadline.setDate(LocalDate.now().plusDays(7));
        
        testDeadline.setSubject(testSubject);
    }

    @Test
    @DisplayName("Deve retornar lista de prazos quando a disciplina pertence ao usuário")
    void getDeadlinesBySubjectIdAndUserEmail_Success() {
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        when(deadlineRepository.findBySubject(testSubject))
                .thenReturn(List.of(testDeadline));

        List<Deadline> results = deadlineService.getDeadlinesBySubjectIdAndUserEmail(subjectId, userEmail);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testDeadline, results.get(0));

        verify(subjectService).getSubjectByIdAndUserEmail(subjectId, userEmail);
        verify(deadlineRepository).findBySubject(testSubject);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar prazos se a disciplina não pertencer ao usuário")
    void getDeadlinesBySubjectIdAndUserEmail_SubjectNotFound() {
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deadlineService.getDeadlinesBySubjectIdAndUserEmail(subjectId, userEmail);
        });

        assertEquals("Disciplina não encontrada ou não pertence ao usuário.", exception.getMessage());
        
        verify(deadlineRepository, never()).findBySubject(any());
    }

    @Test
    @DisplayName("Deve retornar Optional de Deadline quando tudo for válido")
    void getDeadlineByIdAndSubjectAndUser_Success() {
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        when(deadlineRepository.findById(deadlineId))
                .thenReturn(Optional.of(testDeadline));

        Optional<Deadline> result = deadlineService.getDeadlineByIdAndSubjectAndUser(deadlineId, subjectId, userEmail);

        assertTrue(result.isPresent(), "O Optional não deveria estar vazio.");
        assertEquals(testDeadline, result.get());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio se o prazo não for encontrado")
    void getDeadlineByIdAndSubjectAndUser_DeadlineNotFound() {
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        when(deadlineRepository.findById(deadlineId))
                .thenReturn(Optional.empty());

        Optional<Deadline> result = deadlineService.getDeadlineByIdAndSubjectAndUser(deadlineId, subjectId, userEmail);

        assertTrue(result.isEmpty(), "O Optional deveria estar vazio.");
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio se o prazo pertencer a outra disciplina")
    void getDeadlineByIdAndSubjectAndUser_DeadlineMismatchSubject() {
        Subject otherSubject = new Subject();
        otherSubject.setId(99L);
        
        testDeadline.setSubject(otherSubject); 

        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        when(deadlineRepository.findById(deadlineId))
                .thenReturn(Optional.of(testDeadline));
                
        Optional<Deadline> result = deadlineService.getDeadlineByIdAndSubjectAndUser(deadlineId, subjectId, userEmail);

        assertTrue(result.isEmpty(), "O Optional deveria estar vazio pois o prazo não bate com a disciplina.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar prazo se a disciplina não pertencer ao usuário")
    void getDeadlineByIdAndSubjectAndUser_SubjectNotFound() {
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deadlineService.getDeadlineByIdAndSubjectAndUser(deadlineId, subjectId, userEmail);
        });

        assertEquals("Disciplina não encontrada ou não pertence ao usuário.", exception.getMessage());
        
        verify(deadlineRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve criar e salvar um novo prazo com sucesso")
    void createDeadline_Success() {
        Deadline newDeadline = new Deadline();
        newDeadline.setName("Novo Prazo");
        newDeadline.setDate(LocalDate.now().plusDays(10));

        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        
        when(deadlineRepository.save(any(Deadline.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); 

        ArgumentCaptor<Deadline> deadlineCaptor = ArgumentCaptor.forClass(Deadline.class);

        Deadline savedDeadline = deadlineService.createDeadline(subjectId, newDeadline, userEmail);

        assertNotNull(savedDeadline);
        assertEquals("Novo Prazo", savedDeadline.getName());

        verify(deadlineRepository).save(deadlineCaptor.capture());
        
        Deadline capturedDeadline = deadlineCaptor.getValue();
        assertEquals(testSubject, capturedDeadline.getSubject(), "A disciplina correta deve ser associada ao prazo antes de salvar.");
        assertEquals(testSubject, savedDeadline.getSubject(), "O prazo retornado deve conter a disciplina associada.");
    }
    
    @Test
    @DisplayName("Deve atualizar um prazo existente com sucesso")
    void updateDeadline_Success() {
        Deadline updatedData = new Deadline(); 
        updatedData.setName("Prazo Atualizado");
        updatedData.setDate(LocalDate.now().plusDays(5));

        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        when(deadlineRepository.findById(deadlineId))
                .thenReturn(Optional.of(testDeadline)); 
                
        when(deadlineRepository.save(any(Deadline.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
                
        Deadline result = deadlineService.updateDeadline(deadlineId, subjectId, updatedData, userEmail);

        assertNotNull(result);
        
        assertEquals(deadlineId, result.getId()); 
        assertEquals("Prazo Atualizado", result.getName()); 
        assertEquals(LocalDate.now().plusDays(5), result.getDate()); 
        assertEquals(testSubject, result.getSubject()); 

        ArgumentCaptor<Deadline> captor = ArgumentCaptor.forClass(Deadline.class);
        verify(deadlineRepository).save(captor.capture());
        assertEquals("Prazo Atualizado", captor.getValue().getName());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar se o prazo não for encontrado")
    void updateDeadline_NotFound() {
        Deadline updatedData = new Deadline();
        
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        when(deadlineRepository.findById(deadlineId))
                .thenReturn(Optional.empty()); 

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deadlineService.updateDeadline(deadlineId, subjectId, updatedData, userEmail);
        });

        assertEquals("Prazo não encontrado ou inválido para esta disciplina/usuário.", exception.getMessage());
        
        verify(deadlineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar um prazo com sucesso")
    void deleteDeadline_Success() {
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.of(testSubject));
        when(deadlineRepository.findById(deadlineId))
                .thenReturn(Optional.of(testDeadline)); 

        doNothing().when(deadlineRepository).delete(any(Deadline.class));

        deadlineService.deleteDeadline(deadlineId, subjectId, userEmail);

        verify(deadlineRepository, times(1)).delete(testDeadline);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar se o prazo não for encontrado")
    void deleteDeadline_NotFound() {
        when(subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deadlineService.deleteDeadline(deadlineId, subjectId, userEmail);
        });

        assertEquals("Prazo não encontrado ou inválido para esta disciplina/usuário.", exception.getMessage());
        
        verify(deadlineRepository, never()).delete(any());
    }
}