package com.unversity.programm.servicesTest;

import com.unversity.programm.Repository.GroupRepository;
import com.unversity.programm.Repository.ScheduleRepository;
import com.unversity.programm.Repository.StudentRepository;
import com.unversity.programm.dto.DeleteGroupsCourseTimeRequest;
import com.unversity.programm.dto.GroupDto;
import com.unversity.programm.dto.ScheduleDto;
import com.unversity.programm.dto.UpdateGroupCourseTimeRequest;
import com.unversity.programm.entity.Course;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.Schedule;
import com.unversity.programm.entity.Teacher;
import com.unversity.programm.entity.mapper.GroupMapper;
import com.unversity.programm.entity.mapper.ScheduleMapper;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;


@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private GroupService groupService;
    @Mock
    private GroupMapper groupMapper;
    private Group group;
    private Schedule schedule;
    private Teacher teacher;
    private Course course;
    @BeforeEach
    void setUp(){
        course = new Course();
        course.setId(1L);
        group = new Group();
        group.setId(1L);
        group.setGroupName("Math");
        teacher = new Teacher();
        teacher.setId(1L);
        schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setId(1L);
        schedule.setStartTime(LocalDateTime.of(2026,10,21,10,20));
        schedule.setEndTime(LocalDateTime.of(2026,10,21,10,20).plusHours(1));
        schedule.setTeacher(teacher);
        schedule.setCourse(course);
    }
    @Test
    void createGroup(){
        String name = "Business Group";
        when(groupRepository.existsByGroupName(name)).thenReturn(false);
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
            Group group1 = invocation.getArgument(0);
            group1.setId(1L);
            return group1;
        });
        when(groupMapper.toGroupDto(any(Group.class))).thenReturn(new GroupDto(1L,name));
        GroupDto expectingResult = new GroupDto(1L,name);
        GroupDto newGroup = groupService.createNewGroup(name);
        assertEquals(expectingResult,newGroup);
        verify(groupRepository,times(1)).existsByGroupName(name);
        verify(groupRepository,times(1)).save(any(Group.class));
        verify(groupMapper,times(1)).toGroupDto(any(Group.class));
    }
    @Test
    void deleteGroup(){
        when(studentRepository.countStudentsWithOnlyOneThisGroup(1L)).thenReturn(0L);
        when(groupRepository.deleteGroupById(1L)).thenReturn(1);
        String expectedResult = String.format("Группа с таким id = %d удалена",1L);
        String result = groupService.deleteGroupById(1L);
        assertEquals(expectedResult,result);
        verify(studentRepository,times(1)).countStudentsWithOnlyOneThisGroup(1L);
        verify(groupRepository,times(1)).deleteGroupById(1L);
    }
    @Test
    void getCoursesScheduleForGroup(){
        when(groupRepository.existsById(group.getId())).thenReturn(true);
        when(scheduleRepository.findByGroupId(group.getId(), PageRequest.of(0,20, Sort.by("startTime")))).thenReturn(new PageImpl<>(List.of(schedule)));
        when(scheduleMapper.toScheduleDto(any(Schedule.class))).thenReturn(new ScheduleDto(1L,1L,schedule.getId(), course.getId(), schedule.getStartTime(),schedule.getEndTime()));
        Page<ScheduleDto> expectingResult = new PageImpl<>(List.of(new ScheduleDto(1L,1L,schedule.getId(),course.getId(),schedule.getStartTime(),schedule.getEndTime())));
        Page<ScheduleDto> result = groupService.getCoursesScheduleForGroup(group.getId(),0,20);
        assertEquals(expectingResult,result);
    }
    @Test
    void updateGroupCourseTime(){
        UpdateGroupCourseTimeRequest updateGroupTime = new UpdateGroupCourseTimeRequest(
                course.getId(), schedule.getStartTime(),LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        when(scheduleRepository.findGroupsCourseByStartTime(group.getId(),
                updateGroupTime.courseId(), updateGroupTime.oldStartTime())).thenReturn(Optional.of(schedule));

        when(scheduleRepository.existsGroupConflict(group.getId(), schedule.getId(),
                updateGroupTime.newStartTime(),updateGroupTime.newStartTime()
                        .plus(Duration.between(schedule.getStartTime(), schedule.getEndTime())))).thenReturn(false);

        when(scheduleRepository.existsTeacherConflict(teacher.getId(), schedule.getId(),updateGroupTime.newStartTime(),updateGroupTime.newStartTime()
                .plus(Duration.between(schedule.getStartTime(), schedule.getEndTime())))).thenReturn(false);

        when(scheduleMapper.toScheduleDto(any(Schedule.class))).thenReturn(new ScheduleDto(schedule.getId(), group.getId(),
                teacher.getId(), course.getId(), updateGroupTime.newStartTime(),updateGroupTime.newEndTime()));

        ScheduleDto expectingResult = new ScheduleDto(schedule.getId(), group.getId(), teacher.getId(),
                course.getId(), updateGroupTime.newStartTime(),updateGroupTime.newEndTime());
        ScheduleDto result = groupService.updateGroupCourseTime(group.getId(), updateGroupTime);
        assertEquals(expectingResult,result);
    }

    @Test
    void deleteGroupCourseTime(){
        DeleteGroupsCourseTimeRequest deleteGroupTime = new DeleteGroupsCourseTimeRequest(course.getId(), schedule.getStartTime());
        when(scheduleRepository.findGroupsCourseByStartTime(group.getId(), deleteGroupTime.courseId(),deleteGroupTime.startTime())).thenReturn(Optional.of(schedule));
        doNothing().when(scheduleRepository).delete(schedule);
        String expectedResult = String.format("Занятия группы с id = %d в курсе с courseId = %d в %s удалена",
                group.getId(),deleteGroupTime.courseId(),deleteGroupTime.startTime());
        String result = groupService.deleteGroupCourseTime(group.getId(), deleteGroupTime);
        assertEquals(expectedResult,result);
    }


}
