package io.matoshri.bdayreminder.config;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.entity.PersonRequest;
import io.matoshri.bdayreminder.service.PersonService;
import io.matoshri.bdayreminder.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
@Slf4j
public class AppConfig {

    ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    static final String PATTERN = "yyyy-MM-dd";
    @Autowired
    PersonService service;

    @PostConstruct
    public void post() throws Exception {
        readWriteLock.readLock().lock();
        String file = ClassLoader.getSystemResource("dummy/dummy.csv").getFile();
        try (FileReader reader = new FileReader(file);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            String[] lines;
            while ((lines = csvReader.readNext()) != null) {
                String name = lines[0];
                LocalDate localDate = LocalDate.parse(lines[1], DateTimeFormatter.ofPattern(PATTERN));
                String date = localDate.format(AppUtils.getFormatter());
                PersonRequest request = new PersonRequest(0, name, date);

                service.saveNewBirthdayPerson(request);
            }
        } catch (FileNotFoundException e) {
            log.error("file not found", e);
        } catch (IOException e) {
            log.error("IOException", e);
        } catch (CsvValidationException e) {
            log.error("CSV Validation Exception", e);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("lookup-");
        executor.initialize();
        return executor;
    }

}
