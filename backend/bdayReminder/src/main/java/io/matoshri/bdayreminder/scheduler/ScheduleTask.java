package io.matoshri.bdayreminder.scheduler;

import io.matoshri.bdayreminder.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ScheduleTask {

    @Autowired
    PersonService service;

    @Scheduled(cron = "0 1 0 ? * *")
    public void schedulerTask() {
        log.info("Scheduler task started at {}", LocalDateTime.now());

        service.generateCSVFile();
        service.generateJSONFile();

        log.info("Scheduler task ended at {}", LocalDateTime.now());
    }

}


