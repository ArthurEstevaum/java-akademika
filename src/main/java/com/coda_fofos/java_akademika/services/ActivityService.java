package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.enums.ActivityStatus;
import com.coda_fofos.java_akademika.repositories.ActivityRepository;
import enities.Activity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    private ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Activity findById(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
    }

    public Activity findByName(String name) {
        return activityRepository.findByName(name).orElseThrow(() -> new RuntimeException("Nenhuma atividade encontrada."));
    }

    @Transactional
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity updateActivityStatus(Long activityId, ActivityStatus status) {
        Activity activity = findById(activityId);

        activity.setStatus(status);
        return activityRepository.save(activity);
    }
}
