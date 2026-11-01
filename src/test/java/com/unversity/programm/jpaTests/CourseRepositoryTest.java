package com.unversity.programm.jpaTests;

import com.unversity.programm.Repository.CourseRepository;
import com.unversity.programm.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;
    @Test
    void existsByName(){
        Course course = new Course();
        course.setName("AI");
        courseRepository.save(course);
        assertTrue(courseRepository.existsByName(course.getName()));
    }
    @Test
    void existsByNameButNotById(){
        Course course = new Course();
        course.setName("AI");
        courseRepository.save(course);
        assertTrue(courseRepository.existsByNameAndIdNot(course.getName(), course.getId()+1L));
    }
    @Test
    void findCourseWithGroups(){
        Course course = new Course();
        course.setName("AI");
        courseRepository.save(course);
        assertTrue(courseRepository.findCourseAndHisGroupsByCourseId(course.getId()).isPresent());
    }
}
