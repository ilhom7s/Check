package com.unversity.programm.entity.mapper;

import com.unversity.programm.dto.CourseDto;
import com.unversity.programm.dto.CourseDtoWithGroups;
import com.unversity.programm.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {CourseMapper.class})
public interface CourseMapper {
    CourseDto toCourseDto(Course course);
    CourseDtoWithGroups toCourseDtoWithGroups(Course course);
}
