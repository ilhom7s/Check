package com.unversity.programm.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teacher",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"name", "last_name"},
                name = "unq_teacher_name_lastname"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    @Column(name = "name")
    private String name;
    @EqualsAndHashCode.Include
    @Column(name = "last_name")
    private String lastName;

}
