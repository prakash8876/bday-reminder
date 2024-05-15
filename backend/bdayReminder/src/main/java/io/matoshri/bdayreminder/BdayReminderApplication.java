package io.matoshri.bdayreminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BdayReminderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BdayReminderApplication.class, args);
	}

}
