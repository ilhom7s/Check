package com.unversity.programm.servicesTest;

import com.unversity.programm.Repository.ScheduleRepository;
import com.unversity.programm.Repository.TeacherRepository;
import com.unversity.programm.dto.CreateTeacherRequestDto;
import com.unversity.programm.dto.ScheduleDto;
import com.unversity.programm.dto.TeacherDto;
import com.unversity.programm.dto.UpdateTeacherRequest;
import com.unversity.programm.entity.Schedule;
import com.unversity.programm.entity.Teacher;
import com.unversity.programm.entity.mapper.ScheduleMapper;
import com.unversity.programm.entity.mapper.TeacherMapper;
import com.unversity.programm.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
    @InjectMocks
    private TeacherService teacherService;
    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private TeacherMapper teacherMapper;

    private Teacher teacher;

    @BeforeEach
    void setUp(){
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Ilhomjon");
        teacher.setLastName("Sultonov");
    }

    @Test
    void createTeacher(){
        CreateTeacherRequestDto createTeacher = new CreateTeacherRequestDto(teacher.getName(),teacher.getLastName());
        TeacherDto expectingTeacher = new TeacherDto(teacher.getId(), teacher.getName(), teacher.getLastName());
        when(teacherRepository.existsByNameAndLastName(teacher.getName(), teacher.getLastName())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> {
            Teacher savedTeacher =  invocation.getArgument(0);
            return teacher;
        });
        when(teacherMapper.toTeacherDto(any(Teacher.class))).thenReturn(expectingTeacher);
        TeacherDto result = teacherService.createNewTeacher(createTeacher);
        assertEquals(expectingTeacher,result);
        verify(teacherRepository,times(1)).save(any(Teacher.class));
        verify(teacherRepository,times(1)).existsByNameAndLastName(teacher.getName(), teacher.getLastName());
        verify(teacherMapper,times(1)).toTeacherDto(any(Teacher.class));

    }
    @Test
    void updateTeacher(){
        String newName = "Alena";
        String newLastName = "Pavlova";
        UpdateTeacherRequest updateTeacher = new UpdateTeacherRequest(newName,newLastName);
        when(teacherRepository.existsByNameAndLastNameAndIdNot(newName,newLastName, teacher.getId())).thenReturn(false);
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        doNothing().when(teacherRepository).flush();
        TeacherDto expectingResult = new TeacherDto(teacher.getId(), newName,newLastName);
        when(teacherMapper.toTeacherDto(any(Teacher.class))).thenReturn(expectingResult);
        TeacherDto result = teacherService.updateTeacher(teacher.getId(), updateTeacher);
        assertEquals(expectingResult,result);
        verify(teacherRepository,times(1)).existsByNameAndLastNameAndIdNot(newName,newLastName, teacher.getId());
        verify(teacherRepository,times(1)).findById(teacher.getId());
    }
    @Test
    void deleteTeacher(){
        when(scheduleRepository.existsByTeacherIdAndEndTimeAfter(eq(teacher.getId()),any(LocalDateTime.class))).thenReturn(false);
        doNothing().when(scheduleRepository).deleteTeachersPastByTeacherId(eq(teacher.getId()),any(LocalDateTime.class));
        when(teacherRepository.deleteTeacherById(teacher.getId())).thenReturn(1);
        String expectedResult = String.format("Учитель с id = %d удален",teacher.getId());
        String result = teacherService.deleteTeacher(teacher.getId());
        assertEquals(expectedResult,result);
        verify(scheduleRepository,times(1)).existsByTeacherIdAndEndTimeAfter(eq(teacher.getId()),any(LocalDateTime.class));
        verify(scheduleRepository,times(1)).deleteTeachersPastByTeacherId(eq(teacher.getId()),any(LocalDateTime.class));
        verify(teacherRepository,times(1)).deleteTeacherById(teacher.getId());
    }

    @Test
    void getTeachersSchedule(){
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setTeacher(teacher);
        Page<Schedule> expectedPage = new PageImpl<>(List.of(schedule));
        when(scheduleRepository.findTeachersScheduleFromNow(eq(teacher.getId()),any(LocalDateTime.class),eq(PageRequest.of(0,20)))).thenReturn(expectedPage);
        ScheduleDto scheduleDto = new ScheduleDto(1L,null, teacher.getId(), null,null,null);
        Page<ScheduleDto> expectingResult = new PageImpl<>(List.of(scheduleDto));
        when(scheduleMapper.toScheduleDto(any(Schedule.class))).thenReturn(scheduleDto);
        Page<ScheduleDto> result = teacherService.getTeachersScheduleFromNow(teacher.getId(), 0,20);
        assertEquals(expectingResult,result);
        assertEquals(1,result.getTotalPages());
        assertEquals(1,result.getTotalElements());

    }
}
