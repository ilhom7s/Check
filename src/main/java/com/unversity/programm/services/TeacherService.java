package com.unversity.programm.services;

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
import com.unversity.programm.exceptions.AlreadyInBaseException;
import com.unversity.programm.exceptions.ConflictException;
import com.unversity.programm.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Slf4j
@Service
@AllArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final ScheduleMapper scheduleMapper;
    private final ScheduleRepository scheduleRepository;
    private final TeacherMapper teacherMapper;

    @Transactional
    public TeacherDto createNewTeacher(CreateTeacherRequestDto createTeacher){
        String name = createTeacher.name();
        String lastName = createTeacher.lastName();
        log.debug("Создание преподователя: name={}, lastName={}",
                name,lastName);

        if(teacherRepository.existsByNameAndLastName(name, lastName)){
            log.warn("Преподователь не создан, уже существует");
            throw new AlreadyInBaseException(
                    String.format("Преподователь с именем %s и фамилией %s уже есть "
                            ,name,lastName));
        }

        Teacher teacher = new Teacher();
        teacher.setName(createTeacher.name());
        teacher.setLastName(createTeacher.lastName());
        try {
            return teacherMapper.toTeacherDto(teacherRepository.save(teacher));
        } catch (DataIntegrityViolationException e) {
            log.warn("Преподаватель не создан, уже существует (гонка)");
            throw new AlreadyInBaseException(String.format("Преподаватель с именем %s и фамилией %s уже есть", name, lastName));
        }
    }
    @Transactional
    public TeacherDto updateTeacher(Long id, UpdateTeacherRequest updateTeacher){
        log.debug("Измениние преподователя с id = {}",id);
        String newName= updateTeacher.newName();
        String newLastName = updateTeacher.newLastName();
        if(teacherRepository.existsByNameAndLastNameAndIdNot(newName,newLastName,id)){
            log.warn("Изменение не произошло выброшено исключения");
            throw new AlreadyInBaseException(String.format("Преподователь с именем %s и фамилией %s уже есть ",newName,newLastName));
        }
        Teacher teacher = teacherRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("Преподователь с id = %d не найден",id)));
        teacher.setName(updateTeacher.newName());
        teacher.setLastName(updateTeacher.newLastName());
        try{
            teacherRepository.flush();
            log.info("Данные учителя c id = {} изменены",id);
            return teacherMapper.toTeacherDto(teacher);
        }catch (DataIntegrityViolationException exception){
            log.warn("Учитель уже существует другая транзакция заняла имя");
            throw new AlreadyInBaseException(String.format("Преподователь с именем %s и фамилией %s уже есть ",newName,newLastName));
        }
    }

    @Transactional
    public String deleteTeacher(Long id){
        log.debug("Удаление преподователя с id = {}",id);
        LocalDateTime now = LocalDateTime.now();
        if(scheduleRepository.existsByTeacherIdAndEndTimeAfter(id,now)){
            log.warn("Удаление не произошло причина конфликт");
            throw new ConflictException(
                    String.format("Нельзя удалять переподователя с таким id = %d так как у него(е) есть пары!",id));
        }
        scheduleRepository.deleteTeachersPastByTeacherId(id,now);
        int deletedTeacher = teacherRepository.deleteTeacherById(id);
        if(deletedTeacher<0){
            throw new NotFoundException(String.format("Преподователь с таким id = %d не найден",id));
        }
        log.info("Учитель удален с id = {}",id);
        return String.format("Учитель с id = %d удален",id);
    }

    public Page<ScheduleDto> getTeachersScheduleFromNow(Long id, int page, int size){
        log.debug("Запрос вывода учителя с их расписанием");
        Pageable pageable = PageRequest.of(page,size);
        Page<Schedule> schedules = scheduleRepository.findTeachersScheduleFromNow(id,LocalDateTime.now(),pageable);
        log.info("Выведено распиание учителя с id = {}",id);
        return schedules.map(scheduleMapper::toScheduleDto);
    }
}
