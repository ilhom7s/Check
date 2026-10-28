package com.unversity.programm.services;
import com.unversity.programm.Repository.GroupRepository;
import com.unversity.programm.Repository.StudentRepository;
import com.unversity.programm.dto.AddStudentsInGroupRequest;
import com.unversity.programm.dto.ChangeStudentRequestDto;
import com.unversity.programm.dto.CreateStudentRequestDto;
import com.unversity.programm.dto.StudentDto;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.Student;
import com.unversity.programm.mapper.DtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.unversity.programm.exceptions.AlreadyInBaseException;
import com.unversity.programm.exceptions.StudentFoundException;
import com.unversity.programm.exceptions.OneOrManyNotFoundException;
import com.unversity.programm.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final DtoMapper dtoMapper;
    private final GroupService groupService;

    @Transactional
    public StudentDto addNewStudent(CreateStudentRequestDto request) {
        log.debug("Создание студента: name={}, lastName={}, groupIds={}",
                request.name(), request.lastName(), request.groupIds());

        studentRepository.findByNameAndLastName(request.name(), request.lastName())
                .ifPresent(s -> {
                    log.warn("Студент не создан, уже существует: id={}", s.getId());
                    throw new StudentFoundException(String.valueOf(s.getId()));
                });

        List<Group> groups = groupRepository.findAllById(request.groupIds());
        Set<Long> missingIds = new HashSet<>(request.groupIds());
        groups.forEach(g -> missingIds.remove(g.getId()));

        if (!missingIds.isEmpty()) {
            log.warn("Студент не создан, группы не найдены: missingIds={}", missingIds);
            throw new OneOrManyNotFoundException(missingIds);
        }

        Student newStudent = new Student();
        newStudent.setName(request.name());
        newStudent.setLastName(request.lastName());
        newStudent.getGroups().addAll(groups);
        log.info("Студент создан");
        return dtoMapper.toStudentDto(studentRepository.save(newStudent));
    }

    @Transactional
    public String deleteStudentById(Long id){
        log.debug("Удаление студента: id={}", id);
        studentRepository.delete(findStudent(id));
        log.info("Студент удалён: id={}", id);
        return String.format("Студен с id = %d удален",id);
    }

    @Transactional
    public StudentDto changeStudent(ChangeStudentRequestDto changeStudentRequestDto,Long id){
        log.debug("Изменение студента: id={}, oldGroupId={}, newGroupId={}",
                id, changeStudentRequestDto.oldGroupId(), changeStudentRequestDto.newGroupId());

        Long oldGroupId = changeStudentRequestDto.oldGroupId();
        Long newGroupId = changeStudentRequestDto.newGroupId();
        if((oldGroupId==null)!=(newGroupId==null)){
            log.warn("Студент id={} не изменён: передан только один из oldGroupId и newGroupId", id);
            throw new IllegalArgumentException("Аргументы missed");
        }

        Student student = findStudent(id);
        String newName = changeStudentRequestDto.name() != null ? changeStudentRequestDto.name() : student.getName();
        String newLastName = changeStudentRequestDto.lastName() != null ? changeStudentRequestDto.lastName() : student.getLastName();

        if(studentRepository.existsByNameAndLastNameAndIdNot(newName,newLastName,id)){
            log.warn("Студент id={} не изменён: пара {} {} уже занята", id, newName, newLastName);
            throw  new AlreadyInBaseException(String.format("Студент с таким именем =  %s и фамилией = %s уже в базе",newName,newLastName));
        }
        student.setName(newName);
        student.setLastName(newLastName);
        Set<Group> studentGroups = student.getGroups();

        if(oldGroupId!=null) {
            Group oldGroup = groupService.findGroupById(changeStudentRequestDto.oldGroupId());
            if (!studentGroups.remove(oldGroup)) {
                log.warn("Студент id={} не изменён: не состоит в группе id={}", id, oldGroupId);
                throw new NotFoundException("Студент не состоит в группе " + oldGroup.getId());
            }
            Group newGroup = groupService.findGroupById(changeStudentRequestDto.newGroupId());
            studentGroups.add(newGroup);
            log.debug("Студент id={}: группа {} заменена на {}", id, oldGroupId, newGroupId);
        }
            studentRepository.save(student);
            log.info("Студент изменён: id={}", id);
            return dtoMapper.toStudentDto(student);
    }

    public Student findStudent(Long id){
        Student student = studentRepository.findById(id).orElseThrow(
                ()-> {
                    log.warn("Студент не найден: id={}", id);
                    return new NotFoundException(String.format("Студент с таким id = %d не найден", id));
                });
        return student;
    }

    @Transactional(readOnly = true)
    public Page<StudentDto> getStudents(int page, int size){
        log.debug("Список студентов: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page,size, Sort.by("lastName","name"));
        return  studentRepository.findAll(pageable).map(dtoMapper::toStudentDto);
    }
    @Transactional
    public String addStudentsInGroup(Long groupId, AddStudentsInGroupRequest addStudentsInGroup){
        log.debug("Добавление студентов в группу: groupId={}, studentIds={}",
                groupId, addStudentsInGroup.listOfStudentsIds());

       Group group =  groupService.findGroupById(groupId);
       List<Student> students = studentRepository.findAllWithGroupsByIdIn(List.copyOf(addStudentsInGroup.listOfStudentsIds()));
       Set<Long> studentsIds = students.stream().map(Student::getId).collect(Collectors.toSet());
       Set<Long> missingIds = new HashSet<>(addStudentsInGroup.listOfStudentsIds());
       missingIds.removeAll(studentsIds);
       if(!missingIds.isEmpty()){
           log.warn("Студенты не добавлены в группу id={}, не найдены: missingIds={}", groupId, missingIds);
           throw new OneOrManyNotFoundException(missingIds);
       }

       students.forEach(student -> student.getGroups().add(group));
       log.info("Студенты добавлены в группу: groupId={}, количество={}", groupId, students.size());
       return String.format("Студенты добавлены в группу с id = %d",groupId);
    }
}
