package com.unversity.programm.Repository;

import com.unversity.programm.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByTeacherIdAndEndTimeAfter(Long id, LocalDateTime time);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Schedule s WHERE s.teacher.id = :teacherId AND s.endTime <=:now")
    void deleteTeachersPastByTeacherId(@Param("teacherId")Long id,@Param("now")LocalDateTime time);

    @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId AND s.endTime>:now")
    Page<Schedule> findTeachersScheduleFromNow(@Param("teacherId")Long id, @Param("now")LocalDateTime time, Pageable pageable);

    Page<Schedule> findByGroupId(Long groupId,Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.group.id = :groupId AND s.course.id = :courseId AND s.startTime = :startTime")
    Optional<Schedule> findGroupsCourseByStartTime(@Param("groupId") Long id, @Param("courseId")Long courseId, @Param("startTime")LocalDateTime startTime);

    @Query("""
    SELECT COUNT(s) > 0 FROM Schedule s
    WHERE s.group.id = :groupId
      AND s.id <> :excludeId
      AND s.startTime < :newEnd
      AND s.endTime > :newStart
    """)
    boolean existsGroupConflict(@Param("groupId") Long groupId,
                                @Param("excludeId") Long excludeId,
                                @Param("newStart") LocalDateTime newStart,
                                @Param("newEnd") LocalDateTime newEnd);

    @Query("""
    SELECT COUNT(s) > 0 FROM Schedule s
    WHERE s.teacher.id = :teacherId
      AND s.id <> :excludeId
      AND s.startTime < :newEnd
      AND s.endTime > :newStart
    """)
    boolean existsTeacherConflict(@Param("teacherId") Long teacherId,
                                  @Param("excludeId") Long excludeId,
                                  @Param("newStart") LocalDateTime newStart,
                                  @Param("newEnd") LocalDateTime newEnd);

    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.endTime < :thereIsOld")
    int deleteOlderThanOneYear(@Param("thereIsOld") LocalDateTime thereIsOld);
}