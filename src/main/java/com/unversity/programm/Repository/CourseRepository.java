package com.unversity.programm.Repository;

import com.unversity.programm.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.groups WHERE c.id = :id")
    Optional<Course> findCourseAndHisGroupsByCourseId(@Param("id")Long id);

    boolean existsByNameAndIdNot(String name,Long id);

    boolean existsByName(String name);


}