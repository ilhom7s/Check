package com.unversity.programm.dto;


import java.time.LocalDateTime;

public record UpdateGroupCourseTimeRequest(
        Long courseId,
        LocalDateTime oldStartTime,
        LocalDateTime newStartTime,
        LocalDateTime newEndTime
) {
}
