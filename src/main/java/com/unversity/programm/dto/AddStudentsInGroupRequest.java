package com.unversity.programm.dto;


import java.util.Set;

public record AddStudentsInGroupRequest(
        Set<Long> listOfStudentsIds
) {
}
