package io.matoshri.bdayreminder.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.repository.PersonRepository;
import io.matoshri.bdayreminder.util.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static io.matoshri.bdayreminder.util.AppUtils.DEFAULT_DATE;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private final PersonRepository repo;

    @Override
    @Transactional
    public Person saveNewBirthdayPerson(Person person) {
        log.info("Saving new birthday person.... {}", person);
        if (isPersonExists(person)) {
            return repo.findAllByPersonName(person.getPersonName()).get(0);
        }
        String date = AppUtils.validateDate(person.getBirthDate()).orElse(DEFAULT_DATE);
        person.setBirthDate(date);
        return repo.save(person);
    }

    @Override
    public List<Person> findAll() {
        log.info("finding all....");
        return repo.findAll().stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllByPersonName(String personName) {
        log.info("finding all by person name {}", personName);
        return repo.findAllByPersonName(personName).stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllByBirthDate(String birthDate) {
        log.info("finding all by birth date {}", birthDate);
        birthDate = AppUtils.validateDate(birthDate).orElse(DEFAULT_DATE);
        List<Person> collect = repo.findAllByBirthDate(birthDate);
        return collect.parallelStream()
                .sorted(Comparator
                        .comparing(Person::getPersonName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllTodayBirthdayPersons() {
        log.info("finding all today's birth persons");
        String date = LocalDate.now().format(AppUtils.getFormatter());
        return findAllByBirthDate(date);
    }

    @Override
    public List<Person> findAllUpcomingBirthdayPersons() {
        log.info("finding all upcoming birthday persons");
        LocalDate localDate = LocalDate.now().plusDays(1);
        int month = localDate.getMonth().getValue();
        int dayOfMonth = localDate.getDayOfMonth();
        return findAll().stream()
                .filter(person -> {
                    LocalDate date = LocalDate.parse(person.getBirthDate(), AppUtils.getFormatter());
                    return (month <= date.getMonth().getValue()
                            && (dayOfMonth <= date.getDayOfMonth() || month != date.getMonth().getValue()));
                })
                .sorted(Comparator
                        .comparing(Person::getMonthDay)
                        .thenComparing(Person::getPersonName))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public void generateCSVFile() {
        readWriteLock.writeLock().lock();
        log.info("Generating CSV file....");
        Path filePath = AppUtils.getFilePath(AppUtils.getCSVType());
        try (FileWriter fileWriter = new FileWriter(filePath.toFile(), false);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            csvWriter.writeNext(AppUtils.getHeader(), false);
            List<Person> all = findAll();
            all.forEach(p -> csvWriter.writeNext(p.forCSV(), false));
            log.info("CSV file generated at {}", filePath);
        } catch (Exception e) {
            log.error("Exception while generating CSV file", e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void generateJSONFile() {
        readWriteLock.writeLock().lock();
        log.info("Generating JSON file....");
        List<Person> all = findAll();
        try (Writer writer = new FileWriter(AppUtils.getFilePath(AppUtils.getJSONType()).toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(all, writer);
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private boolean isPersonExists(Person person) {
        String personName = person.getPersonName();
        String birthDate = AppUtils.validateDate(person.getBirthDate()).orElse(DEFAULT_DATE);
        return (repo.existsByPersonName(personName) && repo.existsByBirthDate(birthDate));
    }

}
