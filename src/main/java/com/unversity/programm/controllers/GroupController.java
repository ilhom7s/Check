package com.unversity.programm.controllers;


import com.unversity.programm.dto.DeleteGroupsCourseTimeRequest;
import com.unversity.programm.dto.GroupDto;
import com.unversity.programm.dto.ScheduleDto;

import com.unversity.programm.dto.UpdateGroupCourseTimeRequest;
import com.unversity.programm.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;



@RestController
@RequestMapping("/api/v1/groups")
@AllArgsConstructor
@Tag(name = "Group",description = "Управления Группами")
public class GroupController {
    private final GroupService groupService;
    @PostMapping("/create")
    @Operation(summary = "Создать группу")
    public ResponseEntity<GroupDto> createGroup(@RequestParam String name){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupService.createNewGroup(name));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить группу")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(groupService.deleteGroupById(id));
    }
    @GetMapping("/schedule/{id}")
    @Operation(summary = "Вывести расписание группы в определенном курсе")
    public ResponseEntity<Page<ScheduleDto>> getGroupsCourseSchedule(@PathVariable Long id,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size){
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(groupService.getCoursesScheduleForGroup(id,page,size));
    }
    @PostMapping("/course/time/{id}")
    @Operation(summary = "Обновить время проведения курса для группы")
    public ResponseEntity<ScheduleDto> updateGroupCourseTime(@PathVariable Long id, @RequestBody UpdateGroupCourseTimeRequest groupCourseTimeRequest){
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(groupService.updateGroupCourseTime(id,groupCourseTimeRequest));
    }

    @DeleteMapping("/course/time/{id}")
    @Operation(summary = "Удалить занятия курса для группы")
    public ResponseEntity<String> deleteGroupCourseTime(@PathVariable Long id, @RequestBody DeleteGroupsCourseTimeRequest deleteGroupsCourseTime){
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(groupService.deleteGroupCourseTime(id,deleteGroupsCourseTime));
    }


}
