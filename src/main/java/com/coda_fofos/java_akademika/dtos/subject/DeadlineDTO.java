package com.coda_fofos.java_akademika.dtos.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DeadlineDTO(LocalDate date, String name) {
}
