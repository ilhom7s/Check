package com.unversity.programm.jpaTests;

import com.unversity.programm.Repository.StudentRepository;
import com.unversity.programm.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class StudentRepositoryTests {

    @Autowired
    private StudentRepository studentRepository;

    private Student saveStudent() {
        Student student = new Student();
        student.setName("Ilhomjon");
        student.setLastName("Sultonov");
        return studentRepository.save(student);
    }

    @Test
    void savesStudent() {
        Student student = saveStudent();
        assertNotNull(student.getId());
        assertEquals("Ilhomjon", student.getName());
    }

    @Test
    void findStudentByNameAndLastName() {
        Student saved = saveStudent();
        var found = studentRepository.findByNameAndLastName("Ilhomjon", "Sultonov");
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void countStudentsWithOnlyOneGroup() {
        saveStudent();
        assertEquals(0, studentRepository.countStudentsWithOnlyOneThisGroup(1L));
    }

    @Test
    void deleteStudentById() {
        Student saved = saveStudent();
        assertEquals(1, studentRepository.deleteStudentById(saved.getId()));
    }

    @Test
    void existByNameAndLastNameAndNotId() {
        Student saved = saveStudent();
        assertTrue(studentRepository
                .existsByNameAndLastNameAndIdNot("Ilhomjon", "Sultonov", saved.getId() + 1));
    }
}