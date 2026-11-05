package com.unversity.programm.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCourseDto(
        @NotBlank
        String newName,
        String newDescription) {
}
