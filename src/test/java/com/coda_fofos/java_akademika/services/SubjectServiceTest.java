Java

package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.repositories.SubjectRepository;
import com.coda_fofos.java_akademika.repositories.UserRepository;
import enities.Subject;
import enities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubjectService subjectService;

    private User testUser;
    private Subject testSubject;
    private final String userEmail = "test@user.com";
    private final Long userId = 1L;
    private final Long subjectId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail(userEmail);
        testUser.setUsername("testuser");

        testSubject = new Subject();
        testSubject.setId(subjectId);
        testSubject.setName("Test Driven Development");
        testSubject.setUser(testUser);
    }

    @Test
    @DisplayName("Deve retornar a lista de disciplinas do usuário")
    void getSubjectsByUserEmail_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findByUser(testUser)).thenReturn(List.of(testSubject));

        List<Subject> subjects = subjectService.getSubjectsByUserEmail(userEmail);

        assertNotNull(subjects);
        assertEquals(1, subjects.size());
        assertEquals(testSubject, subjects.get(0));
        verify(userRepository).findByEmail(userEmail);
        verify(subjectRepository).findByUser(testUser);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar disciplinas se usuário não for encontrado")
    void getSubjectsByUserEmail_UserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subjectService.getSubjectsByUserEmail(userEmail);
        });

        assertEquals("Usuário não encontrado: " + userEmail, exception.getMessage());
        verify(subjectRepository, never()).findByUser(any());
    }

    @Test
    @DisplayName("Deve retornar Optional da disciplina se ID e email do usuário baterem")
    void getSubjectByIdAndUserEmail_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(testSubject));

        Optional<Subject> result = subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail);

        assertTrue(result.isPresent());
        assertEquals(testSubject, result.get());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar disciplina por ID se usuário não for encontrado")
    void getSubjectByIdAndUserEmail_UserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail);
        });

        assertEquals("Usuário não encontrado: " + userEmail, exception.getMessage());
        verify(subjectRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio se disciplina não for encontrada")
    void getSubjectByIdAndUserEmail_SubjectNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        Optional<Subject> result = subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio se disciplina não pertencer ao usuário")
    void getSubjectByIdAndUserEmail_OwnershipMismatch() {
        User otherUser = new User();
        otherUser.setId(99L);
        otherUser.setEmail("other@user.com");
        testSubject.setUser(otherUser); 

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser)); 
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(testSubject)); 

        Optional<Subject> result = subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve criar e salvar uma nova disciplina para o usuário")
    void createSubject_Success() {
        Subject newSubjectData = new Subject();
        newSubjectData.setName("Nova Disciplina");

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);

        Subject savedSubject = subjectService.createSubject(newSubjectData, userEmail);

        assertNotNull(savedSubject);
        assertEquals("Nova Disciplina", savedSubject.getName());

        verify(subjectRepository).save(subjectCaptor.capture());
        Subject capturedSubject = subjectCaptor.getValue();

        assertEquals(testUser, capturedSubject.getUser());
        assertEquals(testUser, savedSubject.getUser());
    }

    @Test
    @DisplayName("Deve atualizar uma disciplina existente")
    void updateSubject_Success() {
        Subject updatedData = new Subject();
        updatedData.setName("Nome Atualizado");

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(testSubject));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));

        // Esta chamada de método irá falhar em atualizar o nome
        // porque a lógica está faltando no SubjectService.
        subjectService.updateSubject(subjectId, updatedData, userEmail);
        
        // Capturamos o objeto que foi passado para o 'save'
        ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(captor.capture());
        Subject savedSubject = captor.getValue();
        
        // Este teste verifica o comportamento INTENCIONADO.
        // Ele irá falhar com o código fornecido, indicando o bug
        // (a falta de `existingSubject.setName(updatedData.getName());`).
        assertEquals("Nome Atualizado", savedSubject.getName());
        assertEquals(testSubject.getId(), savedSubject.getId());
    }


    @Test
    @DisplayName("Deve lançar exceção ao atualizar disciplina que não pertence ao usuário")
    void updateSubject_NotFoundOrNotOwned() {
        Subject updatedData = new Subject();
        updatedData.setName("Nome Atualizado");

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty()); 

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subjectService.updateSubject(subjectId, updatedData, userEmail);
        });

        assertEquals("Disciplina não encontrada ou não pertence ao usuário.", exception.getMessage());
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar uma disciplina com sucesso")
    void deleteSubject_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(testSubject));

        doNothing().when(subjectRepository).delete(testSubject);

        subjectService.deleteSubject(subjectId, userEmail);

        verify(subjectRepository, times(1)).delete(testSubject);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar disciplina que não pertence ao usuário")
    void deleteSubject_NotFoundOrNotOwned() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subjectService.deleteSubject(subjectId, userEmail);
        });

        assertEquals("Disciplina não encontrada ou não pertence ao usuário.", exception.getMessage());
        verify(subjectRepository, never()).delete(any());
    }
}