package com.unversity.programm.Repository;

import com.unversity.programm.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Long> {
    Optional<Group> findByGroupName(String groupName);
    boolean existsByGroupName(String groupName);



}
