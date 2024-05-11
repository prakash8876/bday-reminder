package io.matoshri.bdayreminder.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.exception.ApiException;
import io.matoshri.bdayreminder.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repo;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Person saveNewBirthdayPerson(Person person) {
        log.info("Saving new birthday person.... {}", person);
        String date = LocalDate.parse(person.getBirthDate()).format(formatter);
        person.setBirthDate(date);
        Person savedPerson = repo.save(person);
        return savedPerson;
    }

    @Override
    public List<Person> findAll() {
        log.info("finding all....");
        return repo.findAll();
    }

    @Override
    public List<Person> findAllByPersonName(String personName) {
        log.info("finding all by person name {}", personName);
        return repo.findAllByPersonName(personName);
    }

    @Override
    public List<Person> findAllByBirthDate(String birthDate) {
        log.info("finding all by birth date {}", birthDate);
        return repo.findAllByBirthDate(birthDate);
    }

    @Override
    public List<Person> findAllTodayBirthdayPersons() {
        log.info("finding all today's birth persons");
        LocalDate today = LocalDate.now();
//        findAll().stream().filter()
        return null;
    }

    @Override
    public List<Person> findAllUpcomingBirthdayPersons() {
        log.info("finding all upcoming birthday persons");
        return null;
    }

    @Override
    public void generateCSVFile() {
        log.info("Generating CSV file....");
    }

    @Override
    public void generateJSONFile() {
        log.info("Generating JSON file....");
    }
}
