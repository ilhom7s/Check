package com.unversity.programm.services;

import com.unversity.programm.Repository.CourseRepository;
import com.unversity.programm.dto.CourseDto;
import com.unversity.programm.dto.CourseDtoWithGroups;
import com.unversity.programm.dto.CreateNewCourseRequestDto;
import com.unversity.programm.dto.UpdateCourseDto;
import com.unversity.programm.entity.Course;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.mapper.CourseMapper;
import com.unversity.programm.exceptions.AlreadyInBaseException;
import com.unversity.programm.exceptions.GroupAlreadyInCourseException;
import com.unversity.programm.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final GroupService groupService;
    private final CourseMapper courseMapper;

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
    public String  addGroupInCourse(Long courseId,Long groupId) {
        log.debug("Добавление группы в курс id={}", courseId);
        Course course = findCourseById(courseId);
        Group group = groupService.findGroupById(groupId);
        if (course.getGroups().contains(group)) {
            log.warn("Группа не добавилась так как уже в этом курсе groupId={}", groupId);
            throw new GroupAlreadyInCourseException(String.format("Группа  с id = %d уже в этом курсе", groupId));
        }
        course.getGroups().add(group);
        log.info("Группа c id={} добавилась в курс name={}", groupId, course.getName());
        return String.format("Группа c id = %d добавлена в курс %s", groupId, course.getName());
      }


    @Transactional
    public CourseDto createNewCourse(CreateNewCourseRequestDto createNewCourseRequest){
        String newCourseName = createNewCourseRequest.name();
        log.debug("Создание нового курса name={}",newCourseName);
        if(courseRepository.existsByName(newCourseName)){
            log.warn("Курс не создан так как уже существует name={}",newCourseName);
            throw new AlreadyInBaseException(String.format("Курс с таким именем %s уже сущетсвует в базе",createNewCourseRequest.name()));
        }
        Course course = new Course();
        course.setName(newCourseName);
        course.setDescription(createNewCourseRequest.description());
        try{
            log.info("Курс создан с именем={}",newCourseName);
            return courseMapper.toCourseDto(courseRepository.save(course));
        }
        catch (DataIntegrityViolationException e){
            log.warn("Курс не создан, имя занято параллельным запросом name={}", newCourseName);
            throw new AlreadyInBaseException(String.format("Курс с именем %s уже существует", newCourseName));
        }
    }

    @Transactional
    public CourseDto changeCourse(Long courseId, UpdateCourseDto updateCourseDto){
        log.debug("Обновление данных курса id={}",courseId);
        Course course = findCourseById(courseId);
        if(courseRepository.existsByNameAndIdNot(updateCourseDto.newName(),courseId)){
            log.warn("Курс не обновился. Имя {} уже существует",updateCourseDto.newName());
            throw new AlreadyInBaseException(String.format("Курс с названием '%s' уже существует",updateCourseDto.newName()));
        }
        course.setName(updateCourseDto.newName());
        course.setDescription(updateCourseDto.newDescription());
        log.info("Курс с id={} обновлен",courseId);
        return courseMapper.toCourseDto(course);
    }

    @Transactional(readOnly = true)
    public Page<CourseDtoWithGroups> getAllCoursesWithGroups(int page, int size){
        log.debug("Вывод курсов с их группами");
        log.info("Найдены курс с их группами");
        return courseRepository.findAll(PageRequest.of(page,size,Sort.by("id"))).map(courseMapper::toCourseDtoWithGroups);
    }
}
