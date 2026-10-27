package com.unversity.programm.dto;

import java.time.LocalDateTime;

public record DeleteGroupsCourseTimeRequest(
        Long courseId,
        LocalDateTime startTime
) {
}
