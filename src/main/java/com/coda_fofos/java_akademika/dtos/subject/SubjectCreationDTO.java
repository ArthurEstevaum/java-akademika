package com.coda_fofos.java_akademika.dtos.subject;

import com.estevaum.akademikaapi.enums.Days;
import com.estevaum.akademikaapi.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubjectCreationDTO(@NotBlank String name, Short quarter, @NotNull Status status, String syllabus, String teacher, List<Days> days, List<DeadlineDTO> deadlines) {
}
