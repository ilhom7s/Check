package com.unversity.programm.controllerTests;

import com.unversity.programm.constants.ControllerConstants;
import com.unversity.programm.controllers.GroupController;
import com.unversity.programm.dto.DeleteGroupsCourseTimeRequest;
import com.unversity.programm.dto.GroupDto;
import com.unversity.programm.dto.ScheduleDto;
import com.unversity.programm.dto.UpdateGroupCourseTimeRequest;
import com.unversity.programm.services.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(GroupController.class)
public class GroupControllerTest {
    @MockitoBean
    private GroupService groupService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createGroup() throws Exception{
        String groupName = "Business";
        GroupDto expectingResult = new GroupDto(1L,groupName);
        when(groupService.createNewGroup(groupName)).thenReturn(expectingResult);
        mockMvc.perform(post("/api/v1/groups/create")
                .param("name",groupName))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectingResult)));
        verify(groupService,times(1)).createNewGroup(groupName);
    }
    @Test
    void deleteGroupById() throws Exception{
        Long groupId = 1L;
        when(groupService.deleteGroupById(groupId)).thenReturn(String.format("Группа с таким id = %d удалена",groupId));
        mockMvc.perform(delete("/api/v1/groups/{id}",groupId))
                .andExpect(status().isAccepted())
                .andExpect(content().string(String.format("Группа с таким id = %d удалена",groupId)));
        verify(groupService,times(1)).deleteGroupById(groupId);
    }
    @Test
    void getGroupCoursesSchedule() throws Exception{
        Long groupId = 1L;
        int page = Integer.parseInt(ControllerConstants.pageDefaultValue);
        int size = Integer.parseInt(ControllerConstants.pageSizeDefaultValue);
        Page<ScheduleDto> expectingResult = new PageImpl<>(
                List.of(new ScheduleDto(1L,1L,1L,1L,LocalDateTime.now().minusYears(1),LocalDateTime.now()))
        );
        when(groupService.getCoursesScheduleForGroup(groupId,page,size)).thenReturn(expectingResult);
        mockMvc.perform(get("/api/v1/groups/schedule/{id}",groupId)
                .param("page",ControllerConstants.pageDefaultValue)
                .param("size",ControllerConstants.pageSizeDefaultValue))
                .andExpect(status().isFound())
                .andExpect(content().json(objectMapper.writeValueAsString(expectingResult)));
        verify(groupService,times(1)).getCoursesScheduleForGroup(groupId,page,size);
    }
    @Test
    void updateGroupCourseTime() throws Exception{
        Long groupId = 1L;
        UpdateGroupCourseTimeRequest updateTime = new UpdateGroupCourseTimeRequest(
                1L,LocalDateTime.now().minusYears(1),LocalDateTime.now(),LocalDateTime.now().plusDays(2)
        );
        ScheduleDto updatedSchedule = new ScheduleDto(1L,1L,1L,1L,LocalDateTime.now(),LocalDateTime.now().plusDays(2));
        when(groupService.updateGroupCourseTime(groupId,updateTime)).thenReturn(updatedSchedule);
        mockMvc.perform(post("/api/v1/groups/course/time/{id}",groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTime)))
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedSchedule)));
        verify(groupService,times(1)).updateGroupCourseTime(groupId,updateTime);
    }
    @Test
    void deleteGroupCourseTime() throws Exception{
        Long groupId = 1L;
        DeleteGroupsCourseTimeRequest deleteGroupCourseTime = new DeleteGroupsCourseTimeRequest(1L,LocalDateTime.now().minusYears(1));
        String expectingResult = String.format("Занятия группы с id = %d в курсе с courseId = %d в %s удалена",groupId,deleteGroupCourseTime.courseId(),deleteGroupCourseTime.startTime());
        when(groupService.deleteGroupCourseTime(groupId,deleteGroupCourseTime)).thenReturn(expectingResult);
        mockMvc.perform(delete("/api/v1/groups/course/time/{id}",groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteGroupCourseTime)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(expectingResult));
        verify(groupService,times(1)).deleteGroupCourseTime(groupId,deleteGroupCourseTime);
    }

}
