package com.unversity.programm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.util.Set;

@Entity
@Table(name = "groups")
@NoArgsConstructor
@Getter
@Setter
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "group_name",unique = true)
    private String groupName;

    @ManyToMany(mappedBy = "groups",fetch = FetchType.LAZY)
    private Set<Student> students;

    @ManyToMany(mappedBy = "groups",fetch = FetchType.LAZY)
    private Set<Course> courses;

}
