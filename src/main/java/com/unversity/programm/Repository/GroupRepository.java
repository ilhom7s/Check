package com.unversity.programm.Repository;

import com.unversity.programm.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface GroupRepository extends JpaRepository<Group,Long> {
    boolean existsByGroupName(String groupName);
    @Modifying
    @Query("DELETE FROM Group g WHERE g.id = :id")
    int deleteGroupById(@Param("id") Long id);
}
