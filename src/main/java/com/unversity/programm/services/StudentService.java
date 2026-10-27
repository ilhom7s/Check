package com.unversity.programm.services;
import com.unversity.programm.Repository.GroupRepository;
import com.unversity.programm.Repository.StudentRepository;
import com.unversity.programm.dto.AddStudentsInGroupRequest;
import com.unversity.programm.dto.ChangeStudentRequestDto;
import com.unversity.programm.dto.CreateStudentRequestDto;
import com.unversity.programm.dto.StudentDto;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.Student;
import com.unversity.programm.exceptions.*;
import com.unversity.programm.mapper.DtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final DtoMapper dtoMapper;
    private final GroupService groupService;


    @Transactional
    public StudentDto addNewStudent(CreateStudentRequestDto request) {
        studentRepository.findByNameAndLastName(request.name(), request.lastName())
                .ifPresent(s -> {
                    throw new StudentFoundException(String.valueOf(s.getId()));
                });

        List<Group> groups = groupRepository.findAllById(request.groupIds());

        Set<Long> missingIds = new HashSet<>(request.groupIds());
        groups.forEach(g -> missingIds.remove(g.getId()));
        if (!missingIds.isEmpty()) {
            throw new OneOrManyNotFoundException(missingIds);
        }

        Student newStudent = new Student();
        newStudent.setName(request.name());
        newStudent.setLastName(request.lastName());
        newStudent.getGroups().addAll(groups);

        return dtoMapper.toStudentDto(studentRepository.save(newStudent));
    }

    @Transactional
    public String deleteStudentById(Long id){
        studentRepository.delete(findStudent(id));
        return String.format("Студен с id = %d удален",id);
    }

    @Transactional
    public StudentDto changeStudent(ChangeStudentRequestDto changeStudentRequestDto,Long id){
        Long oldGroupId = changeStudentRequestDto.oldGroupId();
        Long newGroupId = changeStudentRequestDto.newGroupId();
        if((oldGroupId==null)!=(newGroupId==null)){
            throw new IllegalArgumentException("Аргументы missed");
        }

        Student student = findStudent(id);
        String newName = changeStudentRequestDto.name() != null ? changeStudentRequestDto.name() : student.getName();
        String newLastName = changeStudentRequestDto.lastName() != null ? changeStudentRequestDto.lastName() : student.getLastName();
        if(studentRepository.existsByNameAndLastNameAndIdNot(newName,newLastName,id)){
            throw  new AlreadyInBaseException(String.format("Студент с таким именем =  %s и фамилией = %s уже в базе",newName,newLastName));
        }
        student.setName(newName);
        student.setLastName(newLastName);

        Set<Group> studentGroups = student.getGroups();
        if(oldGroupId!=null) {
            Group oldGroup = groupService.findGroupById(changeStudentRequestDto.oldGroupId());
            if (!studentGroups.remove(oldGroup)) {
                throw new NotFoundException("Студент не состоит в группе " + oldGroup.getId());
            }
            Group newGroup = groupService.findGroupById(changeStudentRequestDto.newGroupId());
            studentGroups.add(newGroup);
        }

            studentRepository.save(student);
            return dtoMapper.toStudentDto(student);

    }

    public Student findStudent(Long id){
        Student student = studentRepository.findById(id).orElseThrow(
                ()->new NotFoundException(String.format("Студент с таким id = %d не найден",id))
        );
        return student;
    }
    @Transactional(readOnly = true)
    public Page<StudentDto> getStudents(int page, int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("lastName","name"));
        return  studentRepository.findAll(pageable).map(dtoMapper::toStudentDto);
    }
    @Transactional
    public String addStudentsInGroup(Long groupId, AddStudentsInGroupRequest addStudentsInGroup){
       Group group =  groupService.findGroupById(groupId);

       List<Student> students = studentRepository.findAllWithGroupsByIdIn(List.copyOf(addStudentsInGroup.listOfStudentsIds()));

       Set<Long> studentsIds = students.stream().map(Student::getId).collect(Collectors.toSet());

       Set<Long> missingIds = new HashSet<>(addStudentsInGroup.listOfStudentsIds());

       missingIds.removeAll(studentsIds);

       if(!missingIds.isEmpty()){
           throw new OneOrManyNotFoundException(missingIds);
       }

       students.forEach(student -> student.getGroups().add(group));
       return String.format("Студенты добавлены в группу с id = %d",groupId);
    }



}
