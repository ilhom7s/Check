package com.unversity.programm.servicesTest;

import com.unversity.programm.Repository.ScheduleRepository;
import com.unversity.programm.services.scheduler.CleanUpTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {
    @InjectMocks
    private CleanUpTask cleanUpTask;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Test
    void cleanAllOlderOneYear(){
        when(scheduleRepository.deleteOlderThanOneYear(any(LocalDateTime.class))).thenReturn(2);
        cleanUpTask.removeAllSchedule();
        verify(scheduleRepository,times(1)).deleteOlderThanOneYear(any(LocalDateTime.class));
    }
}
