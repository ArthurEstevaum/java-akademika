package com.coda_fofos.java_akademika.dtos;

import jakarta.validation.constraints.NotBlank;

public record PromptRequestDTO(@NotBlank String prompt) {
}
