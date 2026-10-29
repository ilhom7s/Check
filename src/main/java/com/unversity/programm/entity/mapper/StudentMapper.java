package com.unversity.programm.entity.mapper;
import com.unversity.programm.dto.StudentDto;

import com.unversity.programm.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {StudentMapper.class})
public interface StudentMapper {
    StudentDto toStudentDto (Student student);
}
