package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.dtos.subject.SubjectCreationDTO;
import com.coda_fofos.java_akademika.dtos.subject.SubjectUpdateDTO;
import com.coda_fofos.java_akademika.repositories.DayRepository;
import com.coda_fofos.java_akademika.repositories.DeadlineRepository;
import com.coda_fofos.java_akademika.repositories.SubjectRepository;
import com.coda_fofos.java_akademika.repositories.UserRepository; // Pode ser necessário para buscar o usuário
import enities.Day;
import enities.Deadline;
import enities.Subject;
import enities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço para a lógica de negócio relacionada a Disciplinas (Subjects).
 */
@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final DayRepository dayRepository;
    private final UserRepository userRepository;
    private final DeadlineRepository deadlineRepository;

    SubjectService(SubjectRepository subjectRepository, UserRepository userRepository, DayRepository dayRepository, DeadlineRepository deadlineRepository) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.dayRepository = dayRepository;
        this.deadlineRepository = deadlineRepository;
    }

    public Subject createSubject(SubjectCreationDTO requestData, String userEmail) {

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NoSuchElementException("Nenhum usuário encontrado com este id"));
        Subject subject = new Subject(user, requestData.name(), requestData.status(), requestData.quarter(), requestData.teacher(), requestData.syllabus());

        List<Day> dayList = dayRepository.findAll();

        Set<Day> subjectDays = dayList.stream().filter(day -> requestData.days().contains(day.getDay())).collect(Collectors.toSet());
        subject.setDays(subjectDays);

        List<Deadline> deadlines = requestData.deadlines().stream().map(deadlineDTO -> new Deadline(deadlineDTO.name(), deadlineDTO.date(), subject)).toList();

        subjectRepository.save(subject);
        deadlineRepository.saveAll(deadlines);

        return subject;
    }

    public List<Subject> getAllUserSubjects(String userEmail) {
        return subjectRepository.findAllByUserEmail(userEmail);
    }

    public Subject getUserSubject(Long id, String userEmail) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Nenhuma disciplina encontrada com este ID"));
        String subjectUserEmail = subject.getUser().getEmail();

        if(!Objects.equals(userEmail, subjectUserEmail)) {
            throw new BadCredentialsException("Acesso negado a esta disciplina: Ela não pertence à você");
        }

        return subject;
    }

    public Subject updateUserSubject(Long id, String userEmail, SubjectUpdateDTO requestData) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Nenhuma disciplina encontrada com este ID"));
        String subjectUserEmail = subject.getUser().getEmail();

        if(!Objects.equals(userEmail, subjectUserEmail)) {
            throw new BadCredentialsException("Acesso negado a esta disciplina: Ela não pertence à você");
        }

        List<Day> dayList = dayRepository.findAll();

        Set<Day> subjectDays = dayList.stream().filter(day -> requestData.days().contains(day.getDay())).collect(Collectors.toSet());

        List<Deadline> deadlines = requestData.deadlines().stream().map(deadlineDTO -> new Deadline(deadlineDTO.name(), deadlineDTO.date(), subject)).toList();

        subject.setDays(subjectDays);
        subject.setName(requestData.name());
        subject.setTeacher(requestData.teacher());
        subject.setStatus(requestData.status());
        deadlineRepository.saveAll(deadlines);

        return subjectRepository.save(subject);
    }

    public boolean deleteSubject(Long id, String userEmail) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Nenhuma disciplina encontrada com este ID"));
        String subjectUserEmail = subject.getUser().getEmail();

        if(!Objects.equals(userEmail, subjectUserEmail)) {
            throw new BadCredentialsException("Acesso negado a esta disciplina: Ela não pertence à você");
        }

        subjectRepository.delete(subject);
        return true;
    }
}