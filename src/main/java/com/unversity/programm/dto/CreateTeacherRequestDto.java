package com.unversity.programm.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTeacherRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String lastName
) {
}
