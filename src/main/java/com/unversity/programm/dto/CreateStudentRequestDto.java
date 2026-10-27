package com.unversity.programm.dto;

import java.util.Set;

public record CreateStudentRequestDto(

        String name,
        String lastName,
        Set<Long> groupIds

) {
}
