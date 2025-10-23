package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.repositories.SubjectRepository;
import com.coda_fofos.java_akademika.repositories.UserRepository; // Pode ser necessário para buscar o usuário
import enities.Subject;
import enities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Serviço para a lógica de negócio relacionada a Disciplinas (Subjects).
 */
@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository; // Para associar disciplinas a usuários

    /**
     * Busca todas as disciplinas de um usuário específico.
     * @param userEmail Email do usuário.
     * @return Lista de disciplinas do usuário.
     * @throws RuntimeException Se o usuário não for encontrado.
     */
    public List<Subject> getSubjectsByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail)); // Considere exceções mais específicas
        return subjectRepository.findByUser(user);
    }

    /**
     * Busca uma disciplina específica pelo ID, verificando se pertence ao usuário.
     * @param subjectId ID da disciplina.
     * @param userEmail Email do usuário proprietário.
     * @return Optional contendo a disciplina se encontrada e pertencer ao usuário.
     */
    public Optional<Subject> getSubjectByIdAndUserEmail(Long subjectId, String userEmail) {
         User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));

         Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);

         // Verifica se a disciplina existe E se pertence ao usuário correto
         if (subjectOpt.isPresent() && subjectOpt.get().getUser().equals(user)) {
             return subjectOpt;
         }
         return Optional.empty(); // Ou lançar exceção de "não encontrado" ou "não autorizado"
    }

    /**
     * Cria uma nova disciplina para um usuário.
     * (Receberia um DTO com os dados da disciplina como parâmetro)
     * @param subjectData // DTO com dados da nova disciplina (nome, status, etc.)
     * @param userEmail Email do usuário que está criando a disciplina.
     * @return A disciplina criada e salva.
     * @throws RuntimeException Se o usuário não for encontrado.
     */
    @Transactional
    public Subject createSubject( /* SubjectCreationDTO subjectData */ Subject subjectData, String userEmail) {
         User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));

         // Criar a entidade Subject a partir do DTO
         // Exemplo simples (assumindo que subjectData é uma entidade Subject já com alguns dados):
         subjectData.setUser(user); // Associa ao usuário

         // Validar regras de negócio (ex: nome duplicado para o mesmo usuário?)

         return subjectRepository.save(subjectData);
    }

    /**
     * Atualiza uma disciplina existente.
     * (Receberia um DTO com os dados atualizados)
     * @param subjectId ID da disciplina a atualizar.
     * @param updatedData // DTO com os dados atualizados.
     * @param userEmail Email do usuário proprietário.
     * @return A disciplina atualizada.
     * @throws RuntimeException Se a disciplina não for encontrada ou não pertencer ao usuário.
     */
    @Transactional
    public Subject updateSubject(Long subjectId, /* SubjectUpdateDTO updatedData */ Subject updatedData, String userEmail) {
        Subject existingSubject = getSubjectByIdAndUserEmail(subjectId, userEmail)
                 .orElseThrow(() -> new RuntimeException("Disciplina não encontrada ou não pertence ao usuário."));

        // Atualizar os campos da existingSubject com base no updatedData
        // Ex: existingSubject.setName(updatedData.getName());
        //     existingSubject.setStatus(updatedData.getStatus());
        // ...

        return subjectRepository.save(existingSubject); // Salva as alterações
    }

     /**
     * Apaga uma disciplina.
     * @param subjectId ID da disciplina a apagar.
     * @param userEmail Email do usuário proprietário.
     * @throws RuntimeException Se a disciplina não for encontrada ou não pertencer ao usuário.
     */
    @Transactional
    public void deleteSubject(Long subjectId, String userEmail) {
        Subject subjectToDelete = getSubjectByIdAndUserEmail(subjectId, userEmail)
                 .orElseThrow(() -> new RuntimeException("Disciplina não encontrada ou não pertence ao usuário."));

        subjectRepository.delete(subjectToDelete);
    }

}