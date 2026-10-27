package com.unversity.programm.dto;

public record ChangeStudentRequestDto(
        String name,
        String lastName,
        Long oldGroupId,
        Long newGroupId
) {
}
