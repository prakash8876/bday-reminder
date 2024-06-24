package io.matoshri.bdayreminder.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.entity.PersonRequest;
import io.matoshri.bdayreminder.repository.PersonRepository;
import io.matoshri.bdayreminder.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional(readOnly = true)
public class PersonService {

    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    final PersonRepository repo;

    public PersonService(PersonRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Person saveNewBirthdayPerson(PersonRequest personRequest) {
        String date = AppUtils.validateDate(personRequest.getBirthDate());
        Person newPerson = Person.builder()
                .personName(personRequest.getPersonName())
                .birthDate(date)
                .build();
        if (isPersonExists(newPerson)) {
            return newPerson;
        }

        synchronized (this) {
            Person saved = repo.save(newPerson);
            log.info("new birthdate created {}", saved);
            return saved;
        }
    }

    public List<Person> findAll() {
        try {
            TimeUnit.MILLISECONDS.sleep(3000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Person::getPersonName))
                .collect(Collectors.toList());
    }

    public List<Person> findAllByPersonName(String personName) {
        return repo.findAllByPersonName(personName);
    }

    public List<Person> findAllByBirthDate(String birthDate) {
        List<Person> finalList = new LinkedList<>();
        birthDate = AppUtils.validateDate(birthDate);
        List<Person> collect = repo.findAllByBirthDate(birthDate);
        List<Person> matching = getAllMatchingDate(birthDate);
        finalList.addAll(collect);
        finalList.addAll(matching);
        return finalList.stream()
                .sorted(Comparator.comparing(Person::getPersonName))
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

    public List<Person> findAllTodayBirthdayPersons() {
        String date = LocalDate.now().format(AppUtils.getFormatter());
        return findAllByBirthDate(date);
    }

    public List<Person> findAllUpcomingBirthdayPersons() {
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
                .limit(10)
                .collect(Collectors.toList());
    }

    public void generateCSVFile() {
        readWriteLock.writeLock().lock();
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

    public void generateJSONFile() {
        readWriteLock.writeLock().lock();
        try (Writer writer = new FileWriter(AppUtils.getFilePath(AppUtils.getJSONType()).toFile())) {
            List<Person> all = findAll();
            gson.toJson(all, writer);
            log.info("JSON file generate....");
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private boolean isPersonExists(Person person) {
        String personName = person.getPersonName();
        String birthDate = AppUtils.validateDate(person.getBirthDate());
        return repo.existsByPersonName(personName) && repo.existsByBirthDate(birthDate);
    }

}

