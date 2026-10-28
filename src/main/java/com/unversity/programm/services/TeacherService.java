package com.unversity.programm.services;

import com.unversity.programm.Repository.ScheduleRepository;
import com.unversity.programm.Repository.TeacherRepository;
import com.unversity.programm.dto.CreateTeacherRequestDto;
import com.unversity.programm.dto.ScheduleDto;
import com.unversity.programm.dto.TeacherDto;
import com.unversity.programm.dto.UpdateTeacherRequest;
import com.unversity.programm.entity.Schedule;
import com.unversity.programm.entity.Teacher;
import com.unversity.programm.exceptions.AlreadyInBaseException;
import com.unversity.programm.exceptions.ConflictException;
import com.unversity.programm.exceptions.NotFoundException;
import com.unversity.programm.mapper.DtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final DtoMapper dtoMapper;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public TeacherDto createNewTeacher(CreateTeacherRequestDto createTeacher){
        if(teacherRepository.existsByNameAndLastName(createTeacher.name(), createTeacher.lastName())){
            throw new AlreadyInBaseException(
                    String.format("Преподователь с именем %s и фамилией %s уже есть "
                            ,createTeacher.name(),createTeacher.lastName()));
        }
        Teacher teacher = new Teacher();
        teacher.setName(createTeacher.name());
        teacher.setLastName(createTeacher.lastName());
        return dtoMapper.toTeacherDto(teacherRepository.save(teacher));
    }
    @Transactional
    public TeacherDto updateTeacher(Long id, UpdateTeacherRequest updateTeacher){
        if(teacherRepository.existsByNameAndLastName(updateTeacher.newName(), updateTeacher.newLastName())){
            throw new AlreadyInBaseException(String.format("Преподователь с именем %s и фамилией %s уже есть ",updateTeacher.newName(),updateTeacher.newLastName()));
        }
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                ()-> new NotFoundException(String.format("Преподователь с id = %d не найден",id))
        );
        teacher.setName(updateTeacher.newName());
        teacher.setLastName(updateTeacher.newLastName());
        return dtoMapper.toTeacherDto(teacher);
    }

    @Transactional
    public String deleteTeacher(Long id){
        if(!teacherRepository.existsById(id)){
            throw new NotFoundException(String.format("Преподователь с таким id = %d не найден",id));
        }
        LocalDateTime now = LocalDateTime.now();
        if(scheduleRepository.existsByTeacherIdAndEndTimeAfter(id,now)){
            throw new ConflictException(
                    String.format("Нельзя удалять переподователя с таким id = %d так как у него(е) есть пары!",id));
        }
        scheduleRepository.deleteTeachersPastByTeacherId(id,now);
        teacherRepository.deleteById(id);
        return String.format("Учитель с id = %d удален",id);
    }

    public Page<ScheduleDto> getTeachersScheduleFromNow(Long id, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Schedule> schedules = scheduleRepository.findTeachersScheduleFromNow(id,LocalDateTime.now(),pageable);
        return schedules.map(dtoMapper::toScheduleDto);
    }
}
