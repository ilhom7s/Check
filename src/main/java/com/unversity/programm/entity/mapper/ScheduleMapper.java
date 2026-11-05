package com.unversity.programm.entity.mapper;

import com.unversity.programm.dto.ScheduleDto;
import com.unversity.programm.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {ScheduleMapper.class})
public interface ScheduleMapper {
    ScheduleDto toScheduleDto(Schedule schedule);
}
