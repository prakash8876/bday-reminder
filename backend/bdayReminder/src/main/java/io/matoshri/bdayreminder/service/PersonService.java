package io.matoshri.bdayreminder.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import io.matoshri.bdayreminder.entity.PersonEntity;
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
    public PersonEntity saveNewBirthdayPerson(PersonRequest personRequest) {
        var date = AppUtils.validateDate(personRequest.getBirthDate());
        var newPerson = PersonEntity.builder()
                .personName(personRequest.getPersonName())
                .birthDate(date)
                .build();
        if (isPersonExists(newPerson)) {
            return newPerson;
        }

        synchronized (this) {
            PersonEntity saved = repo.save(newPerson);
            log.info("new birthdate created {}", saved);
            return saved;
        }
    }

    public List<PersonEntity> findAll() {
        try {
            TimeUnit.MILLISECONDS.sleep(3000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return repo.findAll().stream()
                .sorted(Comparator.comparing(PersonEntity::getPersonName))
                .collect(Collectors.toList());
    }

    public List<PersonEntity> findAllByPersonName(String personName) {
        return repo.findAllByPersonName(personName);
    }

    public List<PersonEntity> findAllByBirthDate(String birthDate) {
        List<PersonEntity> finalList = new LinkedList<>();
        birthDate = AppUtils.validateDate(birthDate);
        List<PersonEntity> collect = repo.findAllByBirthDate(birthDate);
        List<PersonEntity> matching = getAllMatchingDate(birthDate);
        finalList.addAll(collect);
        finalList.addAll(matching);
        return finalList.stream()
                .sorted(Comparator.comparing(PersonEntity::getPersonName))
                .collect(Collectors.toList());
    }

    private List<PersonEntity> getAllMatchingDate(String birthDate) {
        LocalDate ld = LocalDate.parse(birthDate, AppUtils.getFormatter());
        List<PersonEntity> all = findAll();
        Predicate<PersonEntity> predicate = p -> {
            var ild = LocalDate.parse(p.getBirthDate(), AppUtils.getFormatter());
            return ld.getMonth().equals(ild.getMonth()) && ld.getDayOfMonth() == ild.getDayOfMonth();
        };
        return all.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<PersonEntity> findAllTodayBirthdayPersons() {
        var date = LocalDate.now().format(AppUtils.getFormatter());
        return findAllByBirthDate(date);
    }

    public List<PersonEntity> findAllUpcomingBirthdayPersons() {
        LocalDate localDate = LocalDate.now().plusDays(1);
        int month = localDate.getMonth().getValue();
        int dayOfMonth = localDate.getDayOfMonth();
        Predicate<PersonEntity> filter = person -> {
            var date = LocalDate.parse(person.getBirthDate(), AppUtils.getFormatter());
            return (month <= date.getMonth().getValue()
                    && (dayOfMonth <= date.getDayOfMonth() || month != date.getMonth().getValue()));
        };
        return findAll().stream()
                .filter(filter)
                .sorted(Comparator
                        .comparing(PersonEntity::getMonthDay)
                        .thenComparing(PersonEntity::getPersonName))
                .limit(10)
                .collect(Collectors.toList());
    }

    public void generateCSVFile() {
        readWriteLock.writeLock().lock();
        Path filePath = AppUtils.getFilePath(AppUtils.getCSVType());
        try (FileWriter fileWriter = new FileWriter(filePath.toFile(), false)) {
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeNext(AppUtils.getHeader(), false);
            List<PersonEntity> all = findAll();
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
            List<PersonEntity> all = findAll();
            gson.toJson(all, writer);
            log.info("JSON file generate....");
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private boolean isPersonExists(PersonEntity person) {
        String personName = person.getPersonName();
        String birthDate = AppUtils.validateDate(person.getBirthDate());
        return repo.existsByPersonName(personName) && repo.existsByBirthDate(birthDate);
    }

}

