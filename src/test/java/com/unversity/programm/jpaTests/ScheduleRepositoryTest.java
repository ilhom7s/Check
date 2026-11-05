package com.unversity.programm.jpaTests;

import com.unversity.programm.Repository.CourseRepository;
import com.unversity.programm.Repository.GroupRepository;
import com.unversity.programm.Repository.ScheduleRepository;
import com.unversity.programm.Repository.TeacherRepository;
import com.unversity.programm.entity.Course;
import com.unversity.programm.entity.Group;
import com.unversity.programm.entity.Schedule;
import com.unversity.programm.entity.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
public class ScheduleRepositoryTest {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    private Group group;
    private Course course;
    private Teacher teacher;

    @BeforeEach
    void setUp(){
        group = new Group();
        group.setGroupName("Business-Group");
        groupRepository.save(group);
        course = new Course();
        course.setName("AI");
        courseRepository.save(course);
        teacher = new Teacher();
        teacher.setName("Ilhomjon");
        teacher.setLastName("Sultonov");
        teacherRepository.save(teacher);
    }
    private Schedule saveSchedule(Group group,Course course,Teacher teacher,LocalDateTime startTime,LocalDateTime endTime){
        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        return scheduleRepository.save(schedule);
    }

    @Test
    void deleteScheduleThatOlderOneYear(){
        Schedule schedule = saveSchedule(group,course,teacher,LocalDateTime.now(),LocalDateTime.now().plusDays(2));
        assertEquals(0,scheduleRepository.deleteOlderThanOneYear(LocalDateTime.now()));
    }
    @Test
    void teacherHaveClass(){
        Schedule schedule = saveSchedule(group,course,teacher,LocalDateTime.now(),LocalDateTime.now().plusHours(2));
        boolean conflict = scheduleRepository.existsTeacherConflict(teacher.getId(), 0L,LocalDateTime.now(),LocalDateTime.now().plusHours(1));
        assertTrue(conflict);
    }
    @Test
    void GroupConflict(){
        Schedule schedule = saveSchedule(group,course,teacher,LocalDateTime.now(),LocalDateTime.now().plusHours(2));
        boolean groupConflict = scheduleRepository.existsGroupConflict(group.getId(), 0L,LocalDateTime.now().plusHours(1),LocalDateTime.now().plusHours(5));
        assertTrue(groupConflict);
    }
    @Test
    void findGroupCourseByTime(){
        LocalDateTime start = LocalDateTime.of(2026, 11, 3, 10, 0);
        Schedule schedule = saveSchedule(group,course,teacher,start,start.plusHours(1));
        Optional<Schedule> groupSchedule = scheduleRepository.findGroupsCourseByStartTime(group.getId(), course.getId(),schedule.getStartTime());
        assertTrue(groupSchedule.isPresent());
    }
    @Test
    void findTeachersSchedule(){
        LocalDateTime start = LocalDateTime.of(2026, 11, 3, 10, 0);
        Schedule schedule = saveSchedule(group,course,teacher,start,start.plusHours(1));
        Page<Schedule> teacherSchedules = scheduleRepository.findTeachersScheduleFromNow(teacher.getId(), start.minusHours(2), PageRequest.of(0,20, Sort.by("startTime")));
        assertFalse(teacherSchedules.isEmpty());
    }


}
