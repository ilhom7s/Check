package com.unversity.programm.Repository;

import com.unversity.programm.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    boolean existsByNameAndLastName(String name,String lastName);
}
