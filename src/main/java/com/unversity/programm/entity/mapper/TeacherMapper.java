package com.unversity.programm.entity.mapper;

import com.unversity.programm.dto.TeacherDto;
import com.unversity.programm.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {TeacherMapper.class})
public interface TeacherMapper {
    TeacherDto toTeacherDto(Teacher teacher);
}
