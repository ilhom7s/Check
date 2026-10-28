package com.unversity.programm.services;

import com.unversity.programm.Repository.GroupRepository;
import com.unversity.programm.Repository.ScheduleRepository;
import com.unversity.programm.Repository.StudentRepository;
import com.unversity.programm.dto.DeleteGroupsCourseTimeRequest;
import com.unversity.programm.dto.GroupDto;
import com.unversity.programm.dto.ScheduleDto;
import com.unversity.programm.dto.UpdateGroupCourseTimeRequest;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.Schedule;
import com.unversity.programm.exceptions.NotFoundException;
import com.unversity.programm.mapper.DtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import com.unversity.programm.exceptions.AlreadyInBaseException;
import com.unversity.programm.exceptions.GroupDeletionException;
import com.unversity.programm.exceptions.ConflictException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final DtoMapper dtoMapper;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;

    public Group findGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> {
            log.warn("Группа не найдена: id={}", id);
            return new NotFoundException(String.format("Группа с id = %d не найдена", id));
        });
    }

    @Transactional
    public GroupDto createNewGroup(String name){
        log.debug("Создание группы: name={}", name);
        if(groupRepository.existsByGroupName(name)) throw new AlreadyInBaseException(String.format("Группа с  именем = %s уже существует",name));
        log.warn("Группа не создана, имя уже занято: name={}", name);
        Group newGroup = new Group();
        newGroup.setGroupName(name);

        log.info("Группа создана: name={}",name);
        return dtoMapper.toGroupDto(groupRepository.save(newGroup));
    }

    @Transactional
    public String deleteGroupById(Long id){
        log.debug("Удаление группы: id={}",id);
        if(!groupRepository.existsById(id)){
            log.warn("Группа для удаления не найдена: id={}",id);
            throw new NotFoundException(String.format("Группа с таким id = %d не найден",id));
        }
        if(studentRepository.countStudentsWithOnlyOneThisGroup(id)>0){
            log.warn("Группа id={} не удалена: у студентов она единственная",id);
            throw new GroupDeletionException("Невозможно удалить группу так как у этой группы есть студенты у которых единственная группа");
        }
        studentRepository.deleteStudentGroupLinksByGroupId(id);
        groupRepository.deleteById(id);
        log.info("Группа удалена: id={}", id);
        return String.format("Группа с таким id = %d удалена",id);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleDto> getCoursesScheduleForGroup(Long groupId, int page, int size){
        log.debug("График группы: groupId={}, page={}, size={}", groupId, page, size);
        if(!groupRepository.existsById(groupId)){
            log.warn("График не получен, группа не найдена: id={}", groupId);
            throw  new NotFoundException(String.format("Группа с таким id = %d не найден",groupId));
        }
        return scheduleRepository.findByGroupId(groupId, PageRequest.of(page,size, Sort.by("startTime"))).map(dtoMapper::toScheduleDto);
    }

    @Transactional
    public ScheduleDto updateGroupCourseTime(Long id, UpdateGroupCourseTimeRequest updateDto) {
        log.debug("Перенос занятия: groupId={}, courseId={}, oldStart={}, newStart={}",
                id, updateDto.courseId(), updateDto.oldStartTime(), updateDto.newStartTime());

        Schedule schedule = scheduleRepository.findGroupsCourseByStartTime(id, updateDto.courseId(), updateDto.oldStartTime())
                .orElseThrow(() -> {
                    log.warn("Занятие не найдено: groupId={}, courseId={}, start={}", id, updateDto.courseId(), updateDto.oldStartTime());
                    return new NotFoundException(String.format("Не найдено занятие с группой id = %d и курсом id = %d в %s",
                            id, updateDto.courseId(), updateDto.oldStartTime()));});

        Duration duration = Duration.between(schedule.getStartTime(), schedule.getEndTime());
        LocalDateTime newStart = updateDto.newStartTime();
        LocalDateTime newEnd = newStart.plus(duration);

        if (scheduleRepository.existsGroupConflict(id, schedule.getId(), newStart, newEnd)) {
            log.warn("Занятие id={} не перенесено: конфликт у группы id={} в {}", schedule.getId(), id, newStart);
            throw new ConflictException("У группы уже есть занятие в это время");
        }
        if (scheduleRepository.existsTeacherConflict(schedule.getTeacher().getId(), schedule.getId(), newStart, newEnd)){
            log.warn("Занятие id={} не перенесено: преподаватель  занят в {}", schedule.getId(),newStart);
            throw new ConflictException("Преподаватель занят в это время");
        }
        schedule.setStartTime(newStart);
        schedule.setEndTime(newEnd);
        log.info("Занятие перенесено: scheduleId={}, groupId={}, с {} на {}", schedule.getId(), id, updateDto.oldStartTime(), newStart);
        return dtoMapper.toScheduleDto(schedule);
    }

    @Transactional
    public String deleteGroupCourseTime(Long id, DeleteGroupsCourseTimeRequest deleteRequest){
        log.debug("Удаление занятия: groupId={}, courseId={}, start={}",
                id, deleteRequest.courseId(), deleteRequest.startTime());
        Schedule schedule = scheduleRepository.findGroupsCourseByStartTime(id, deleteRequest.courseId(), deleteRequest.startTime())
                .orElseThrow(() ->{
                    log.warn("Занятие для удаления не найдено: groupId={}, courseId={}, start={}", id, deleteRequest.courseId(), deleteRequest.startTime());
                    return new NotFoundException(String.format("Не найдено занятие с группой id = %d и курсом id = %d в %s",
                        id, deleteRequest.courseId(), deleteRequest.startTime()));});

        scheduleRepository.delete(schedule);
        log.info("Занятие удалено: scheduleId={}, groupId={}", schedule.getId(), id);
        return String.format("Занятия группы с id = %d в курсе с courseId = %d в %s удалена",
                id,deleteRequest.courseId(),deleteRequest.startTime());
    }
}
