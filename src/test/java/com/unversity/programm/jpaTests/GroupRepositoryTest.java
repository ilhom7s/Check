package com.unversity.programm.jpaTests;

import com.unversity.programm.Repository.GroupRepository;
import com.unversity.programm.entity.Group;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class GroupRepositoryTest {
    @Autowired
    private GroupRepository groupRepository;
    @Test
    void existsByGroupName(){
        Group group = new Group();
        group.setGroupName("Business");
        groupRepository.save(group);
        assertTrue(groupRepository.existsByGroupName(group.getGroupName()));
    }
    @Test
    void deletGroupById(){
        Group group = new Group();
        group.setGroupName("Business");
        groupRepository.save(group);
        assertEquals(1,groupRepository.deleteGroupById(group.getId()));
    }
    @Test
    void findGroupById(){
        Group group = new Group();
        group.setGroupName("Business");
        groupRepository.save(group);
        assertTrue(groupRepository.findById(group.getId()).isPresent());
    }
}
