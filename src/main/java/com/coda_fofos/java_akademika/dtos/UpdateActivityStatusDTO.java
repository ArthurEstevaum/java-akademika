package com.coda_fofos.java_akademika.dtos;

import com.coda_fofos.java_akademika.enums.ActivityStatus;
import com.coda_fofos.java_akademika.services.ActivityService;

public record UpdateActivityStatusDTO(Long id, ActivityStatus activityStatus) {
}
