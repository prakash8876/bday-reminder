package io.matoshri.bdayreminder.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.repository.PersonRepository;
import io.matoshri.bdayreminder.util.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.matoshri.bdayreminder.util.AppUtils.formatter;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repo;

    @Override
    @Transactional
    public Person saveNewBirthdayPerson(Person person) {
        log.info("Saving new birthday person.... {}", person);
        if (isPersonExists(person)) {
            return repo.findAllByPersonName(person.getPersonName()).get(0);
        }
        String date = LocalDate.parse(person.getBirthDate()).format(formatter);
        person.setBirthDate(date);
        return repo.save(person);
    }

    @Override
    public List<Person> findAll() {
        log.info("finding all....");
        return repo.findAll().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllByPersonName(String personName) {
        log.info("finding all by person name {}", personName);
        return repo.findAllByPersonName(personName).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllByBirthDate(String birthDate) {
        log.info("finding all by birth date {}", birthDate);
        String date = LocalDate.parse(birthDate).format(formatter);
        return repo.findAllByBirthDate(date).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllTodayBirthdayPersons() {
        log.info("finding all today's birth persons");
        String date = LocalDate.now().format(formatter);
        return findAllByBirthDate(date).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllUpcomingBirthdayPersons() {
        log.info("finding all upcoming birthday persons");
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonth().getValue();
        int dayOfMonth = localDate.getDayOfMonth();
        return findAll().stream()
                .filter(person -> {
                    LocalDate date = LocalDate.parse(person.getBirthDate(), formatter);
                    return (month < date.getMonth().getValue() && dayOfMonth <= date.getDayOfMonth());
                })
                .sorted(Comparator.comparingInt(p -> LocalDate.parse(p.getBirthDate(), formatter).getMonth().getValue()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public void generateCSVFile() {
        log.info("Generating CSV file....");
        try {
            Path filePath = AppUtils.getFilePath(AppUtils.CSV);
            FileWriter fileWriter = new FileWriter(filePath.toFile());
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeNext(AppUtils.header, false);
            List<Person> all = findAll();
            all.forEach(p -> {
                csvWriter.writeNext(p.forCSV(), false);
            });
            fileWriter.close();
            log.info("CSV file generated at ", filePath);
        } catch (Exception e) {
            log.error("Exception while generating CSV file", e);
        }
    }

    @Override
    public void generateJSONFile() {
        log.info("Generating JSON file....");
        List<Person> all = findAll();
        try (Writer writer = new FileWriter(AppUtils.getFilePath(AppUtils.JSON).toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(all, writer);
        } catch (Exception e) {
            log.error("Exception", e);
        }

    }

    private boolean isPersonExists(Person person) {
        String personName = person.getPersonName();
        String birthDate = person.getBirthDate();
        return (repo.existsByPersonName(personName) && repo.existsByBirthDate(birthDate));
    }
}
