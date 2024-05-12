package io.matoshri.bdayreminder.config;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Configuration
public class AppConfig {

    @Autowired
    private PersonService service;

    @PostConstruct
    public void post() throws Exception {
        String file = ClassLoader.getSystemResource("dummy/dummy.csv").getFile();
        try (FileReader reader = new FileReader(file);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            String[] lines;
            while ((lines = csvReader.readNext()) != null) {
                Person newPerson = Person.builder().personName(lines[0]).birthDate(lines[1]).build();
                service.saveNewBirthdayPerson(newPerson);
            }
        } catch (FileNotFoundException e) {
            log.error("file not found", e);
        } catch (IOException e) {
            log.error("IOException", e);
        } catch (CsvValidationException e) {
            log.error("CSV Validation Exception", e);
        }
    }
}
