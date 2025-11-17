package com.coda_fofos.java_akademika.dtos.subject;

import com.coda_fofos.java_akademika.enums.Days;
import com.coda_fofos.java_akademika.enums.Status;

import java.util.List;

public record SubjectResponseDTO(Long id, String name, Short quarter, Status status, String syllabus, String teacher, List<Days> days, List<DeadlineDTO> deadlines) {
}
