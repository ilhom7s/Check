package com.unversity.programm.Repository;

import com.unversity.programm.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    boolean existsByNameAndLastName(String name, String lastName);
    boolean existsByNameAndLastNameAndIdNot(String name,String lastName,Long id);
    @Modifying()
    @Query("DELETE FROM Teacher t WHERE t.id = :id")
    int deleteTeacherById(@Param("id")Long id);
}
