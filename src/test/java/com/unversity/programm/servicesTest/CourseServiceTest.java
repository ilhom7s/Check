package com.unversity.programm.servicesTest;

import com.unversity.programm.Repository.CourseRepository;
import com.unversity.programm.dto.CourseDto;
import com.unversity.programm.dto.CourseDtoWithGroups;
import com.unversity.programm.dto.CreateNewCourseRequestDto;
import com.unversity.programm.dto.UpdateCourseDto;
import com.unversity.programm.entity.Course;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.mapper.CourseMapper;
import com.unversity.programm.services.CourseService;
import com.unversity.programm.services.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;


@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private GroupService groupService;
    @Mock
    private CourseMapper courseMapper;
    @InjectMocks
    private CourseService courseService;
    private Long groupId;
    private Group group;
    private Course course;
    private Long courseId;

    @BeforeEach
    void setUp(){
        courseId = 1L;
        groupId = 1L;
        group = new Group();
        group.setId(groupId);
        course = new Course();
        course.setId(1L);
        course.setGroups(new HashSet<>());

    }

    @Test
    void addGroupInCourse(){
        when(groupService.findGroupById(groupId)).thenReturn(group);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        String expectingResult = String.format("Группа c id = %d добавлена в курс %s", groupId, course.getName());
        String result = courseService.addGroupInCourse(courseId,groupId);
        assertEquals(expectingResult,result);
        verify(groupService,times(1)).findGroupById(groupId);
        verify(courseRepository,times(1)).findById(courseId);
    }
    @Test
    void createNewCourse(){
        CreateNewCourseRequestDto createCourse = new CreateNewCourseRequestDto("Business","Class For Business");
        when(courseRepository.existsByName(createCourse.name())).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course savedCourse = invocation.getArgument(0);
            savedCourse.setId(1L);
            return savedCourse;
        });
        CourseDto expectingResult = new CourseDto(1L,"Business","Class For Business");
        when(courseMapper.toCourseDto(any(Course.class))).thenReturn(expectingResult);
        CourseDto result = courseService.createNewCourse(createCourse);
        assertEquals(expectingResult,result);
        verify(courseRepository,times(1)).existsByName(createCourse.name());
        verify(courseRepository,times(1)).save(any(Course.class));
    }
    @Test
    void updateCourse(){
        UpdateCourseDto updateCourse = new UpdateCourseDto("IT","IT Program");
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.existsByNameAndIdNot(updateCourse.newName(),courseId)).thenReturn(false);
        CourseDto expectingResult = new CourseDto(1L,"IT","IT Program");
        when(courseMapper.toCourseDto(any(Course.class))).thenReturn(expectingResult);
        CourseDto result = courseService.changeCourse(courseId,updateCourse);
        assertEquals(expectingResult,result);
    }
    @Test
    void getAllCoursesWithGroups(){
        Page<CourseDtoWithGroups> expectingResult = new PageImpl<>(List.of(new CourseDtoWithGroups(courseId,"IT","IT",null)));
        when(courseRepository.findAll(PageRequest.of(0,20, Sort.by("id")))).thenReturn(new PageImpl<>(List.of(course)));
        when(courseMapper.toCourseDtoWithGroups(course)).thenReturn(new CourseDtoWithGroups(courseId,"IT","IT",null));
        Page<CourseDtoWithGroups> result = courseService.getAllCoursesWithGroups(0,20);
        assertEquals(expectingResult,result);
        verify(courseRepository,times(1)).findAll(PageRequest.of(0,20, Sort.by("id")));
    }
}
