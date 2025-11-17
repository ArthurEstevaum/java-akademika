package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.dtos.UpdateActivityStatusDTO;
import com.coda_fofos.java_akademika.services.ActivityService;
import enities.Activity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<Activity>> findAll() {
        return ResponseEntity.ok(activityService.findAll());
    }

    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity newActivity) {
        return ResponseEntity.ok(activityService.createActivity(newActivity));
    }

    @PutMapping
    public ResponseEntity<Activity> updateActivity(@RequestBody UpdateActivityStatusDTO dto) {
        return ResponseEntity.ok(activityService.updateActivityStatus(dto.id(), dto.activityStatus()));
    }
}
