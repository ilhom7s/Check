package com.unversity.programm.controllerTests;

import com.unversity.programm.constants.ControllerConstants;
import com.unversity.programm.controllers.CourseController;
import com.unversity.programm.dto.*;
import com.unversity.programm.services.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
public class CourseControllerTest {
    @MockitoBean
    private CourseService courseService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    Long id = 1L;

    @Test
    void createCourse() throws Exception{
        CreateNewCourseRequestDto createCourse = new CreateNewCourseRequestDto("Business","Business course");
        CourseDto createdCourse = new CourseDto(id,"Business","Business course");
        when(courseService.createNewCourse(createCourse)).thenReturn(createdCourse);
        mockMvc.perform(post("/api/v1/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCourse)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdCourse)));
        verify(courseService,times(1)).createNewCourse(createCourse);
    }
    @Test
    void updateCourse() throws Exception{
        UpdateCourseDto updateRequest = new UpdateCourseDto("Business","Business class");

        CourseDto updatedCourse = new CourseDto(id,"Business","Business class");
        when(courseService.changeCourse(id,updateRequest)).thenReturn(updatedCourse);
        mockMvc.perform(post("/api/v1/course/update/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedCourse)));
        verify(courseService,times(1)).changeCourse(id,updateRequest);
    }
    @Test
    void addGroupInCourse() throws Exception{
        Long groupId = 1L;
        String resultOfAdding = String.format("Группа c id = %d добавлена в курс", groupId);
        when(courseService.addGroupInCourse(id,groupId)).thenReturn(resultOfAdding);
        mockMvc.perform(post("/api/v1/course/add")
                .param("groupId",String.valueOf(groupId))
                .param("courseId",String.valueOf(id)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(resultOfAdding));
        verify(courseService,times(1)).addGroupInCourse(id,groupId);
    }
    @Test
    void getAllCoursesWithGroups() throws Exception{
        int page = Integer.parseInt(ControllerConstants.pageDefaultValue);
        int size = Integer.parseInt(ControllerConstants.pageSizeDefaultValue);
        Page<CourseDtoWithGroups> allCourses = new PageImpl<>(List.of(
                new CourseDtoWithGroups(id,"Business","Business class",Set.of(new GroupDto(id,"GroupOfGOATS")))
        ));
        when(courseService.getAllCoursesWithGroups(page,size)).thenReturn(allCourses);
        mockMvc.perform(get("/api/v1/course/all")
                .param("page",ControllerConstants.pageDefaultValue)
                .param("size",ControllerConstants.pageSizeDefaultValue))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(allCourses)));
        verify(courseService,times(1)).getAllCoursesWithGroups(page,size);
    }
}
