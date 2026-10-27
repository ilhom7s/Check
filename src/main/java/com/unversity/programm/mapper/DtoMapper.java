package com.unversity.programm.mapper;
import com.unversity.programm.dto.*;
import com.unversity.programm.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {DtoMapper.class})
public interface DtoMapper {
    StudentDto toStudentDto (Student student);
    GroupDto toGroupDto(Group group);
    CourseDto toCourseDto(Course course);
    TeacherDto toTeacherDto(Teacher teacher);
    ScheduleDto toScheduleDto(Schedule schedule);
    CourseDtoWithGroups toCourseDtoWithGroups(Course course);
}
