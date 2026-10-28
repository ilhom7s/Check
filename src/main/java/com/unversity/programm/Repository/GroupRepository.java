package com.unversity.programm.Repository;

import com.unversity.programm.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,Long> {
    boolean existsByGroupName(String groupName);
}
