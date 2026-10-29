package com.unversity.programm.entity.mapper;

import com.unversity.programm.dto.GroupDto;
import com.unversity.programm.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {GroupMapper.class})
public interface GroupMapper {
    GroupDto toGroupDto(Group group);
}
