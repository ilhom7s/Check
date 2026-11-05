package com.unversity.programm.jpaTests;

import com.unversity.programm.Repository.TeacherRepository;
import com.unversity.programm.entity.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher createTeacher(){
        Teacher teacher = new Teacher();
        teacher.setName("Ilhomjon");
        teacher.setLastName("Sultonov");
        return teacherRepository.save(teacher);
    }
    @Test
    void existsByNameAndLastName(){
        Teacher teacher = createTeacher();
        boolean teacherIsExits = teacherRepository.existsByNameAndLastName("Ilhomjon","Sultonov");
        assertEquals(true,teacherIsExits);
    }
    @Test
    void isTeachersNameAndLastNameAlreadyInBase(){
        Teacher teacher = createTeacher();
        boolean teacherIsInBase = teacherRepository.existsByNameAndLastNameAndIdNot(teacher.getName(), teacher.getLastName(), teacher.getId());
        assertEquals(false,teacherIsInBase);
    }
    @Test
    void findTeacher(){
        Teacher teacher = createTeacher();
        Optional<Teacher> foundedTeacher = teacherRepository.findById(teacher.getId());
        assertTrue(foundedTeacher.isPresent());
    }
    @Test
    void deleteTeacherById(){
        Teacher teacher = createTeacher();
        int deletedTeacher = teacherRepository.deleteTeacherById(teacher.getId());
        assertEquals(1,deletedTeacher);
    }
}
