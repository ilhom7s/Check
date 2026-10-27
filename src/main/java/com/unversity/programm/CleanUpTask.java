package com.unversity.programm;

import com.unversity.programm.Repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class CleanUpTask {
    private final ScheduleRepository scheduleRepository;
    @Scheduled(cron = "${app.cleanup.cron}")
    public void removeAllSchedule(){
        LocalDateTime dateTime = LocalDateTime.now().minusYears(1);
        int deleted = scheduleRepository.deleteOlderThanOneYear(dateTime);
        log.info("Удалено {} записей расписаний старше одного года",deleted);
    }
}
