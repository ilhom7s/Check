package com.unversity.programm.dto;

import java.util.Set;

public record CourseDtoWithGroups(
        Long id,
        String name,
        String description,
        Set<GroupDto> groups
) {
}
