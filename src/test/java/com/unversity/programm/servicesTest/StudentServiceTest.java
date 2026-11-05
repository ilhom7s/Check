package com.unversity.programm.servicesTest;

import com.unversity.programm.Repository.GroupRepository;
import com.unversity.programm.Repository.StudentRepository;
import com.unversity.programm.dto.AddStudentsInGroupRequest;
import com.unversity.programm.dto.ChangeStudentRequestDto;
import com.unversity.programm.dto.CreateStudentRequestDto;
import com.unversity.programm.dto.StudentDto;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.Student;
import com.unversity.programm.entity.mapper.StudentMapper;
import com.unversity.programm.services.GroupService;
import com.unversity.programm.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.argThat;


@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private GroupService groupService;

    @InjectMocks
    private StudentService studentService;
    private Student student;
    private Group group1;
    private Group group2;
    private StudentDto studentDto;

    @BeforeEach
    void setUp(){
        student = new Student();
        student.setId(1L);
        student.setName("Ilhomjon");
        student.setLastName("Sultonov");
        group1 = new Group();
        group1.setId(1L);
        group2 = new Group();
        group2.setId(2L);
        student.setGroups(new HashSet<>());
        studentDto = new StudentDto(1L,"Ilhomjon","Sultonov");
    }

    @Test
    void addNewStudent(){
        CreateStudentRequestDto createStudent = new CreateStudentRequestDto("Ilhomjon","Sultonov", Set.of(1L,2L));

        when(groupRepository.findAllById(Set.of(1L,2L))).thenReturn(List.of(group1,group2));
        when(studentRepository.findByNameAndLastName("Ilhomjon","Sultonov")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toStudentDto(any(Student.class))).thenReturn(studentDto);
        StudentDto result = studentService.addNewStudent(createStudent);
        assertEquals("Ilhomjon",result.name());
        assertEquals("Sultonov",result.lastName());
        assertEquals(1L,result.id());
        verify(studentRepository,times(1)).save(argThat(s->s.getGroups().size()==2 && s.getGroups().stream().anyMatch(g->g.getId()==1L)));
    }
    @Test
    void deleteStudentById(){
        Long id = 1L;
        when(studentRepository.deleteStudentById(id)).thenReturn(1);
        String result = studentService.deleteStudentById(id);
        String expectedResult = String.format("Студен с id = %d удален",id);
        assertEquals(expectedResult,result);
        verify(studentRepository,times(1)).deleteStudentById(id);
    }
    @Test
    void updateStudent(){
        Long id = 1L;
        ChangeStudentRequestDto updateStudent = new ChangeStudentRequestDto("Alexsey","Alexseyev",null,null);
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentRepository.existsByNameAndLastNameAndIdNot("Alexsey","Alexseyev",1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            s.setId(1L);
            s.setName("Alexsey");
            s.setLastName("Alexseyev");
            return s;
        });
        when(studentMapper.toStudentDto(any(Student.class))).thenReturn(new StudentDto(1L,"Alexsey","Alexseyev"));
        StudentDto result = studentService.changeStudent(updateStudent,id);
        assertEquals(updateStudent.name(),result.name());
        assertEquals(updateStudent.lastName(),result.lastName());
        verify(studentRepository,times(1)).findById(1L);
        verify(studentRepository,times(1)).existsByNameAndLastNameAndIdNot("Alexsey","Alexseyev",1L);
    }
    @Test
    void getStudents(){
        Pageable pageable = PageRequest.of(0,20, Sort.by("lastName","name"));
        when(studentRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(student)));
        when(studentMapper.toStudentDto(any(Student.class))).thenReturn(studentDto);
        Page<StudentDto> result = studentService.getStudents(0,20);
        assertEquals(1,result.getTotalElements());
        assertEquals(1,result.getTotalPages());
        verify(studentRepository,times(1)).findAll(pageable);
        verify(studentMapper,times(1)).toStudentDto(any(Student.class));
    }
    @Test
    void addStudentsInGroup(){
        AddStudentsInGroupRequest addStudents = new AddStudentsInGroupRequest(Set.of(1L));
        when(studentRepository.findAllWithGroupsByIdIn(List.copyOf(addStudents.listOfStudentsIds()))).thenReturn(List.of(student));
        when(groupService.findGroupById(1L)).thenReturn(group1);
        String result = studentService.addStudentsInGroup(group1.getId(),addStudents);
        String expectingResult = String.format("Студенты добавлены в группу с id = %d",group1.getId());
        assertEquals(expectingResult,result);
        verify(studentRepository,times(1)).findAllWithGroupsByIdIn(List.copyOf(addStudents.listOfStudentsIds()));
        verify(groupService,times(1)).findGroupById(1L);
    }

}
