package com.unversity.programm.services;

import com.unversity.programm.Repository.CourseRepository;
import com.unversity.programm.dto.CourseDto;
import com.unversity.programm.dto.CourseDtoWithGroups;
import com.unversity.programm.dto.CreateNewCourseRequestDto;
import com.unversity.programm.dto.UpdateCourseDto;
import com.unversity.programm.entity.Course;
import com.unversity.programm.exceptions.AlreadyInBaseException;
import com.unversity.programm.exceptions.GroupAlreadyInCourseException;
import com.unversity.programm.exceptions.NotFoundException;
import com.unversity.programm.mapper.DtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final GroupService groupService;
    private final DtoMapper dtoMapper;

    public Course findCourseById(Long id){
         return courseRepository.findById(id).orElseThrow(
                ()->new NotFoundException(String.format("Курс с таким id = %d не найден",id))
        );
    }
    public Course findCourseAndHisGroupsById(Long courseId){
         return courseRepository.findCourseAndHisGroupsByCourseId(courseId).orElseThrow(
                ()->new NotFoundException(String.format("Курс с таким id = %d не найден",courseId))
        );

    }
    @Transactional
    public void  addGroupInCourse(Long courseId,Long groupId){
        Course course = findCourseAndHisGroupsById(courseId);
        if(!course.getGroups().add(groupService.findGroupById(groupId))){
            throw new GroupAlreadyInCourseException(String.format("Группа  с id = %d уже в этом курсе",groupId));
        }
    }
    @Transactional
    public CourseDto createNewCourse(CreateNewCourseRequestDto createNewCourseRequest){
        if(courseRepository.existsByName(createNewCourseRequest.name())){
            throw new AlreadyInBaseException(String.format("Курс с таким именем %s уже сущетсвует в базе",createNewCourseRequest.name()));
        }
        Course course = new Course();
        course.setName(createNewCourseRequest.name());
        course.setDescription(createNewCourseRequest.description());
        return dtoMapper.toCourseDto(courseRepository.save(course));
    }

    @Transactional
    public CourseDto changeCourse(Long courseId, UpdateCourseDto updateCourseDto){
        Course course = findCourseById(courseId);
        if(courseRepository.existsByNameAndIdNot(updateCourseDto.newName(),courseId)){
            throw new AlreadyInBaseException(String.format("Курс с названием '%s' уже существует",updateCourseDto.newName()));
        }
        course.setName(updateCourseDto.newName());
        course.setDescription(updateCourseDto.newDescription());
        return dtoMapper.toCourseDto(course);

    }
    @Transactional(readOnly = true)
    public Page<CourseDtoWithGroups> getAllCoursesWithGroups(int page, int size){
        return courseRepository.findAll(PageRequest.of(page,size,Sort.by("id"))).map(dtoMapper::toCourseDtoWithGroups);
    }






}
