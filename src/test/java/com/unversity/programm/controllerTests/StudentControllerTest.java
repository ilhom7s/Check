package com.unversity.programm.controllerTests;


import com.unversity.programm.controllers.StudentController;
import com.unversity.programm.dto.AddStudentsInGroupRequest;
import com.unversity.programm.dto.ChangeStudentRequestDto;
import com.unversity.programm.dto.CreateStudentRequestDto;
import com.unversity.programm.dto.StudentDto;
import com.unversity.programm.services.StudentService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StudentService studentService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void createStudent() throws Exception {
        CreateStudentRequestDto createStudentRequestDto = new CreateStudentRequestDto("Ilya", "Sultonov", Set.of(1L));
        StudentDto response = new StudentDto(1L, "Ilya", "Sultonov");
        when(studentService.addNewStudent(createStudentRequestDto)).thenReturn(response);

        mockMvc.perform(post("/api/v1/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStudentRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ilya"))
                .andExpect(jsonPath("$.lastName").value("Sultonov"));

        verify(studentService, times(1)).addNewStudent(createStudentRequestDto);
    }
    @Test
    void deleteStudent() throws Exception{
        when(studentService.deleteStudentById(1L)).thenReturn("Студен с id = 1 удален");
        mockMvc.perform(delete("/api/v1/student/{id}",1L))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Студен с id = 1 удален"));
        verify(studentService,times(1)).deleteStudentById(1L);
    }
    @Test
    void changeStudentById() throws Exception{
        ChangeStudentRequestDto changeStudentRequestDto = new ChangeStudentRequestDto("Alex","Musk",1L,2L);
        when(studentService.changeStudent(changeStudentRequestDto,1L)).thenReturn(new StudentDto(1L,"Alex","Musk"));
        mockMvc.perform(post("/api/v1/student/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeStudentRequestDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Musk"));
        verify(studentService,times(1)).changeStudent(changeStudentRequestDto,1L);
    }
    @Test
    void getAllStudents() throws Exception{
        Page<StudentDto> page = new PageImpl<>(List.of());
        when(studentService.getStudents(0,20)).thenReturn(page);
        mockMvc.perform(get("/api/v1/student")
                .param("page","0")
                .param("size","20"))
                .andExpect(status().isOk());
        verify(studentService,times(1)).getStudents(0,20);
    }

    @Test
    void addStudentsInGroup() throws Exception{
        AddStudentsInGroupRequest addStudentsIngroup = new AddStudentsInGroupRequest(Set.of(1L,2L));
        when(studentService.addStudentsInGroup(1L,addStudentsIngroup)).thenReturn("Студенты добавлены в группу с id = 1");
        mockMvc.perform(post("/api/v1/student/add/{groupId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addStudentsIngroup)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Студенты добавлены в группу с id = 1"));
        verify(studentService,times(1)).addStudentsInGroup(1L,addStudentsIngroup);
    }


}