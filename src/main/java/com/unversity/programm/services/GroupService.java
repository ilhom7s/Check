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
import com.unversity.programm.exceptions.*;
import com.unversity.programm.mapper.DtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final DtoMapper dtoMapper;
    private final StudentRepository studentRepository;

    private final ScheduleRepository scheduleRepository;

    public Group findGroupById(Long id){
       Group group =  groupRepository.findById(id)
                .orElseThrow(()->new NotFoundException(String.format("Группа с таким id = %d не найден",id)));
       return group;
    }

    @Transactional
    public GroupDto createNewGroup(String name){
        if(groupRepository.existsByGroupName(name)) throw new AlreadyInBaseException(String.format("Группа с  именем = %s уже существует",name));
        Group newGroup = new Group();
        newGroup.setGroupName(name);
        return dtoMapper.toGroupDto(groupRepository.save(newGroup));
    }

    @Transactional
    public void deleteGroupById(Long id){
        if(!groupRepository.existsById(id)) throw new NotFoundException(String.format("Группа с таким id = %d не найден",id));
        if(studentRepository.countStudentsWithOnlyOneThisGroup(id)>0){
            throw new GroupDeletionException("Невозможно удалить группу так как у этой группы есть студенты у которых единственная группа");
        }
        studentRepository.deleteStudentGroupLinksByGroupId(id);
        groupRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleDto> getCoursesScheduleForGroup(Long groupId, int page, int size){
        if(!groupRepository.existsById(groupId))throw  new NotFoundException(String.format("Группа с таким id = %d не найден",groupId));
        return scheduleRepository.findByGroupId(groupId, PageRequest.of(page,size, Sort.by("startTime"))).map(dtoMapper::toScheduleDto);
    }

    @Transactional
    public ScheduleDto updateGroupCourseTime(Long id, UpdateGroupCourseTimeRequest updateDto) {
        Schedule schedule = scheduleRepository.findGroupsCourseByStartTime(id, updateDto.courseId(), updateDto.oldStartTime())
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено занятие с группой id = %d и курсом id = %d в %s",
                        id, updateDto.courseId(), updateDto.oldStartTime())));

        Duration duration = Duration.between(schedule.getStartTime(), schedule.getEndTime());
        LocalDateTime newStart = updateDto.newStartTime();
        LocalDateTime newEnd = newStart.plus(duration);

        if (scheduleRepository.existsGroupConflict(id, schedule.getId(), newStart, newEnd)) {
            throw new ConflictException("У группы уже есть занятие в это время");
        }
        if (scheduleRepository.existsTeacherConflict(schedule.getTeacher().getId(), schedule.getId(), newStart, newEnd)) {
            throw new ConflictException("Преподаватель занят в это время");
        }

        schedule.setStartTime(newStart);
        schedule.setEndTime(newEnd);
        return dtoMapper.toScheduleDto(schedule);
    }

    @Transactional
    public void deleteGroupCourseTime(Long id, DeleteGroupsCourseTimeRequest deleteRequest){
        Schedule schedule = scheduleRepository.findGroupsCourseByStartTime(id, deleteRequest.courseId(), deleteRequest.startTime())
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено занятие с группой id = %d и курсом id = %d в %s",
                        id, deleteRequest.courseId(), deleteRequest.startTime())));;

        scheduleRepository.delete(schedule);
    }


}
