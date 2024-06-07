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
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.matoshri.bdayreminder.util.AppUtils.DEFAULT_DATE;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final PersonRepository repo;

    @Override
    @Transactional
    public Person saveNewBirthdayPerson(Person person) {
        log.info("Saving new birthday person.... {}", person);
        String date = AppUtils.validateDate(person.getBirthDate());
        person.setBirthDate(date);

        if (isPersonExists(person)) {
            return person;
        }

        synchronized (this) {
            person = repo.save(person);
        }
        return person;
    }

    @Override
    public List<Person> findAll() {
        try {
            Thread.sleep(1000L);
            log.info("findAll() called... {}", LocalDateTime.now());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Person::getPersonName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> findAllByPersonName(String personName) {
        log.info("finding all by person name {}", personName);
        return repo.findAllByPersonName(personName);
    }

    @Override
    public List<Person> findAllByBirthDate(String birthDate) {
        log.info("finding all by birth date {}", birthDate);
        List<Person> finalList = new LinkedList<>();
        birthDate = AppUtils.validateDate(birthDate);
        List<Person> collect = repo.findAllByBirthDate(birthDate);
        List<Person> matching = getAllMatchingDate(birthDate);
        finalList.addAll(collect);
        finalList.addAll(matching);
        return finalList.stream()
                .sorted(Comparator
                        .comparing(Person::getPersonName))
                .collect(Collectors.toList());
    }

    private List<Person> getAllMatchingDate(String birthDate) {
        LocalDate ld = LocalDate.parse(birthDate, AppUtils.getFormatter());
        List<Person> all = findAll();
        Predicate<Person> predicate = p -> {
            LocalDate ild = LocalDate.parse(p.getBirthDate(), AppUtils.getFormatter());
            return ld.getMonth().equals(ild.getMonth()) && ld.getDayOfMonth() == ild.getDayOfMonth();
        };
        return all.stream()
                .filter(predicate)
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
        Predicate<Person> filter = person -> {
            LocalDate date = LocalDate.parse(person.getBirthDate(), AppUtils.getFormatter());
            return (month <= date.getMonth().getValue()
                    && (dayOfMonth <= date.getDayOfMonth() || month != date.getMonth().getValue()));
        };
        return findAll().stream()
                .filter(filter)
                .sorted(Comparator
                        .comparing(Person::getMonthDay)
                        .thenComparing(Person::getPersonName))
//                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public void generateCSVFile() {
        readWriteLock.writeLock().lock();
        log.info("Generating CSV file...., locaked");
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
            log.info("Generating csv file completed...., unlocked");
        }
    }

    @Override
    public void generateJSONFile() {
        readWriteLock.writeLock().lock();
        log.info("Generating JSON file...., locked");
        List<Person> all = findAll();
        try (Writer writer = new FileWriter(AppUtils.getFilePath(AppUtils.getJSONType()).toFile())) {
            gson.toJson(all, writer);
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            readWriteLock.writeLock().unlock();
            log.info("Generating JSON unlocked....");
        }
    }

    private boolean isPersonExists(Person person) {
        String personName = person.getPersonName();
        String birthDate = AppUtils.validateDate(person.getBirthDate());
        return (repo.existsByPersonName(personName)
                && repo.existsByBirthDate(birthDate));
    }

}
