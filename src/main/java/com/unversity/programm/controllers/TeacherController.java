package com.unversity.programm.controllers;

import com.unversity.programm.dto.CreateTeacherRequestDto;
import com.unversity.programm.dto.ScheduleDto;
import com.unversity.programm.dto.TeacherDto;
import com.unversity.programm.dto.UpdateTeacherRequest;
import com.unversity.programm.services.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/teacher")
@AllArgsConstructor
@Tag(name = "Teachers",description = "Управления Учителями")
public class TeacherController {
    private final TeacherService teacherService;
    @PostMapping
    @Operation(summary = "Создать учителя")
    public ResponseEntity<TeacherDto> createTeacher(@RequestBody CreateTeacherRequestDto createTeacherRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teacherService.createNewTeacher(createTeacherRequest));
    }
    @PostMapping("/update/{id}")
    @Operation(summary = "Обновить учителя по id")
    public ResponseEntity<TeacherDto> updateTeacher(@PathVariable Long id, @RequestBody UpdateTeacherRequest updateTeacherRequest){
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(teacherService.updateTeacher(id,updateTeacherRequest));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить преподователя по id")
    public ResponseEntity<String> deleteTeacher(@PathVariable Long id){

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(teacherService.deleteTeacher(id));
    }
    @GetMapping("/schedule/{id}")
    @Operation(summary = "Вывести расписание учителя по id")
    public ResponseEntity<Page<ScheduleDto>> getTeachersSchedule(@PathVariable Long id,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size){
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(teacherService.getTeachersScheduleFromNow(id,page,size));
    }


}
