package com.unversity.programm.controllerTests;


import com.unversity.programm.constants.ControllerConstants;
import com.unversity.programm.controllers.TeacherController;
import com.unversity.programm.dto.*;

import com.unversity.programm.services.TeacherService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TeacherController.class)
public class TeacherControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TeacherService teacherService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTeacher() throws Exception{
        CreateTeacherRequestDto createTeacher = new CreateTeacherRequestDto("Alisa","Valko");
        TeacherDto createdTeacher = new TeacherDto(1L,"Alisa","Valko");
        when(teacherService.createNewTeacher(createTeacher)).thenReturn(createdTeacher);
        mockMvc.perform(post("/api/v1/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTeacher)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdTeacher)));
        verify(teacherService,times(1)).createNewTeacher(createTeacher);
    }
    @Test
    void updateTeacher() throws Exception{
        Long teacherId = 1L;
        UpdateTeacherRequest updateTeacher = new UpdateTeacherRequest("Ilhomjon","Sultonov");
        TeacherDto expectingResult = new TeacherDto(teacherId,updateTeacher.newName(),updateTeacher.newLastName());
        when(teacherService.updateTeacher(teacherId,updateTeacher)).thenReturn(expectingResult);
        mockMvc.perform(post("/api/v1/teacher/update/{id}",teacherId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTeacher)))
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(expectingResult)));
        verify(teacherService,times(1)).updateTeacher(teacherId,updateTeacher);
    }
    @Test
    void deleteTeacherById() throws Exception{
        Long teacherId = 1L;
        when(teacherService.deleteTeacher(teacherId)).thenReturn(String.format("Учитель с id = %d удален",teacherId));
        mockMvc.perform(delete("/api/v1/teacher/{id}",teacherId))
                .andExpect(status().isAccepted())
                .andExpect(content().string(String.format("Учитель с id = %d удален",teacherId)));
        verify(teacherService,times(1)).deleteTeacher(teacherId);
    }
    @Test
    void getTeachersScheduleById() throws Exception{
        Long teacherId = 1L;
        int page =  Integer.parseInt(ControllerConstants.pageDefaultValue);
        int size = Integer.parseInt(ControllerConstants.pageSizeDefaultValue);
        Page<ScheduleDto> expectingResult = new PageImpl<>(List.of());
        when(teacherService.getTeachersScheduleFromNow(teacherId,page,size)).thenReturn(expectingResult);
                mockMvc.perform(get("/api/v1/teacher/schedule/{id}",teacherId))
                .andExpect(status().isFound())
                .andExpect(content().json(objectMapper.writeValueAsString(expectingResult)));
        verify(teacherService,times(1)).getTeachersScheduleFromNow(teacherId,page,size);
    }

}
