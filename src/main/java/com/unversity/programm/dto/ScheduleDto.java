package com.unversity.programm.dto;

import java.time.LocalDateTime;

public record ScheduleDto(
        Long id,
        Long groupId,
        Long teacherId,
        Long courseId,
        LocalDateTime startTime,
        LocalDateTime endTime) {
}
