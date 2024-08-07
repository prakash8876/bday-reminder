package io.matoshri.bdayreminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BdayReminderApplication {

    public static void main(String[] args) {
        System.setProperty("spring.output.ansi.enabled","always");
        SpringApplication.run(BdayReminderApplication.class, args);
    }

}
