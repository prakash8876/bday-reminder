package io.matoshri.bdayreminder.scheduler;

import io.matoshri.bdayreminder.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ScheduleTask {

    @Autowired
    private PersonService service;

    @Scheduled(cron = "0 1 0 ? * *")
    public void schedularTask() {
      log.info("Schedular task started at {}", LocalDateTime.now());

      service.generateCSVFile();
      service.generateJSONFile();

      log.info("Schedular task ended at {}", LocalDateTime.now());
    }

}
