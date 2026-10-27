package com.unversity.programm.controllers;
import com.unversity.programm.dto.CourseDto;
import com.unversity.programm.dto.CourseDtoWithGroups;
import com.unversity.programm.dto.CreateNewCourseRequestDto;
import com.unversity.programm.dto.UpdateCourseDto;
import com.unversity.programm.services.CourseService;
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

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/course")
@Tag(name = "Courses",description = "Управления Курсами")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Создать Курс")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CreateNewCourseRequestDto createCourse){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.createNewCourse(createCourse));
    }
    @PostMapping("/update/{id}")
    @Operation(summary = "Обновить курс")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id, @RequestBody UpdateCourseDto updateCourse){
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(courseService.changeCourse(id,updateCourse));
    }
    @PostMapping("/add")
    @Operation(summary = "Добавить группу в курс")
    public ResponseEntity<String> addGroupInCourse(@RequestParam Long groupId,@RequestParam Long courseId){
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(courseService.addGroupInCourse(courseId,groupId));
    }
    @GetMapping("/all")
    @Operation(summary = "Найти все курсы с группами")
    public ResponseEntity<Page<CourseDtoWithGroups>> getAllCoursesWithGroups(@RequestParam(defaultValue = "0")int page,
                                                                             @RequestParam(defaultValue = "20") int size){
        return ResponseEntity
                .ok(courseService.getAllCoursesWithGroups(page, size));
    }

}
