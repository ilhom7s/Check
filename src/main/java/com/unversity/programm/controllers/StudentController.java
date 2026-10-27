package com.unversity.programm.controllers;

import com.unversity.programm.dto.AddStudentsInGroupRequest;
import com.unversity.programm.dto.ChangeStudentRequestDto;
import com.unversity.programm.dto.CreateStudentRequestDto;
import com.unversity.programm.dto.StudentDto;
import com.unversity.programm.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/student")
@AllArgsConstructor
@Tag(name = "Students",description = "Управления Студентами")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Добавить нового студента")
    public ResponseEntity<StudentDto> addNewStudent(@RequestBody CreateStudentRequestDto createStudentRequestDto){
        StudentDto createdStudent = studentService.addNewStudent(createStudentRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdStudent);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить студента")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id){
        studentService.deleteStudentById(id);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Студент удален успешно");
    }

    @PostMapping("/{id}")
    @Operation(summary = "Изменить студента по id")
    public ResponseEntity<StudentDto> changeStudent(@PathVariable Long id,@RequestBody ChangeStudentRequestDto changeStudent){
         StudentDto changedStudent = studentService.changeStudent(changeStudent,id);
         return ResponseEntity
                 .status(HttpStatus.ACCEPTED)
                 .body(changedStudent);
    }
    @GetMapping
    @Operation(summary = "Вывести студентов по 20 шт")
    public ResponseEntity<Page<StudentDto>> getAllStudents(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "20") int size){
        Page<StudentDto> students = studentService.getStudents(page,size);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(students);

    }
    @PostMapping("/add/{groupId}")
    @Operation(summary = "Добавить студента в группу")
    public ResponseEntity<String> addStudentsInGroups(@PathVariable Long groupId, @RequestBody AddStudentsInGroupRequest addStudentsInGroupRequest){
        studentService.addStudentsInGroup(groupId,addStudentsInGroupRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Студенты успешно добавлены в группу");
    }


}
