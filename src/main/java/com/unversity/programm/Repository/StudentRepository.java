package com.unversity.programm.Repository;

import com.unversity.programm.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> findByNameAndLastName(String name,String lastName);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.groups WHERE s.id IN :ids ORDER BY s.id")
    List<Student> findAllWithGroupsByIdIn(@Param("ids")List<Long> ids);

    @Query("SELECT COUNT(s) FROM Student s JOIN s.groups g WHERE g.id = :groupId AND SIZE(s.groups) = 1")
    Long countStudentsWithOnlyOneThisGroup(@Param("groupId")Long groupId);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM student_groups WHERE group_id = :groupId",nativeQuery = true)
    void deleteStudentGroupLinksByGroupId(@Param("groupId") Long groupId);

    boolean existsByNameAndLastNameAndIdNot(String name,String lastName,Long id);
}
