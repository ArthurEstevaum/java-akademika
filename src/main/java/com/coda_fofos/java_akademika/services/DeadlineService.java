package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.repositories.DeadlineRepository;
// import com.coda_fofos.java_akademika.repositories.SubjectRepository; // <- REMOVIDO
import enities.Deadline;
import enities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class DeadlineService {

    @Autowired
    private DeadlineRepository deadlineRepository;
  

    @Autowired
    private SubjectService subjectService; // Reutilizar a lógica de verificação de posse do Subject

    /**
     * Busca todos os prazos de uma disciplina específica, verificando a posse do usuário.
     * @param subjectId ID da disciplina.
     * @param userEmail Email do usuário.
     * @return Lista de prazos da disciplina.
     * @throws RuntimeException Se a disciplina não for encontrada ou não pertencer ao usuário.
     */
    public List<Deadline> getDeadlinesBySubjectIdAndUserEmail(Long subjectId, String userEmail) {
        // Verifica se o usuário tem acesso à disciplina usando SubjectService
        Subject subject = subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada ou não pertence ao usuário."));

        // Usa apenas o DeadlineRepository para buscar os prazos da disciplina validada
        return deadlineRepository.findBySubject(subject);
    }

    /**
     * Busca um prazo específico pelo ID, verificando se pertence à disciplina e ao usuário.
     * @param deadlineId ID do prazo.
     * @param subjectId ID da disciplina.
     * @param userEmail Email do usuário.
     * @return Optional contendo o prazo, se encontrado e válido.
     */
    public Optional<Deadline> getDeadlineByIdAndSubjectAndUser(Long deadlineId, Long subjectId, String userEmail) {
         // Verifica se o usuário tem acesso à disciplina usando SubjectService
        Subject subject = subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada ou não pertence ao usuário."));

        Optional<Deadline> deadlineOpt = deadlineRepository.findById(deadlineId);

        // Verifica se o prazo existe E se pertence à disciplina correta
        if (deadlineOpt.isPresent() && deadlineOpt.get().getSubject().equals(subject)) {
            return deadlineOpt;
        }
        return Optional.empty();
    }


    /**
     * Cria um novo prazo para uma disciplina.
     * (Receberia um DTO com os dados do prazo).
     * @param subjectId ID da disciplina à qual o prazo pertence.
     * @param deadlineData // DTO com dados do novo prazo (nome, data).
     * @param userEmail Email do usuário.
     * @return O prazo criado.
     * @throws RuntimeException Se a disciplina não for encontrada ou não pertencer ao usuário.
     */
    @Transactional
    public Deadline createDeadline(Long subjectId, /* DeadlineCreationDTO deadlineData */ Deadline deadlineData, String userEmail) {
        // Valida acesso/existência da disciplina via SubjectService
        Subject subject = subjectService.getSubjectByIdAndUserEmail(subjectId, userEmail)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada ou não pertence ao usuário."));

        deadlineData.setSubject(subject); // Associa à disciplina validada

        return deadlineRepository.save(deadlineData);
    }

     /**
     * Atualiza um prazo existente.
     * (Receberia um DTO com os dados atualizados).
     * @param deadlineId ID do prazo a atualizar.
     * @param subjectId ID da disciplina do prazo (pode ser null se não for necessário na validação específica).
     * @param updatedData // DTO com os dados atualizados.
     * @param userEmail Email do usuário.
     * @return O prazo atualizado.
     * @throws RuntimeException Se o prazo/disciplina não for encontrado ou não pertencer ao usuário.
     */
    @Transactional
    public Deadline updateDeadline(Long deadlineId, Long subjectId, /* DeadlineUpdateDTO updatedData */ Deadline updatedData, String userEmail) {
        // A validação de posse é feita em getDeadlineByIdAndSubjectAndUser
        Deadline existingDeadline = getDeadlineByIdAndSubjectAndUser(deadlineId, subjectId, userEmail)
                .orElseThrow(() -> new RuntimeException("Prazo não encontrado ou inválido para esta disciplina/usuário."));

        // Atualizar campos (usando dados de updatedData)
        // Ex: existingDeadline.setName(updatedData.getName());
        //     existingDeadline.setDate(updatedData.getDate());
        // Se receber a entidade direto (como no exemplo atual), precisa copiar os campos:
        existingDeadline.setName(updatedData.getName());
        existingDeadline.setDate(updatedData.getDate());


        return deadlineRepository.save(existingDeadline);
    }

    /**
     * Apaga um prazo.
     * @param deadlineId ID do prazo.
     * @param subjectId ID da disciplina do prazo (pode ser null se não for necessário na validação específica).
     * @param userEmail Email do usuário.
     * @throws RuntimeException Se o prazo/disciplina não for encontrado ou não pertencer ao usuário.
     */
    @Transactional
    public void deleteDeadline(Long deadlineId, Long subjectId, String userEmail) {
        // A validação de posse é feita em getDeadlineByIdAndSubjectAndUser
         Deadline deadlineToDelete = getDeadlineByIdAndSubjectAndUser(deadlineId, subjectId, userEmail)
                .orElseThrow(() -> new RuntimeException("Prazo não encontrado ou inválido para esta disciplina/usuário."));

        deadlineRepository.delete(deadlineToDelete);
    }

}