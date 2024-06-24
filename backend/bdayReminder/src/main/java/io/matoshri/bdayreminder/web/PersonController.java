package io.matoshri.bdayreminder.web;

import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.entity.PersonRequest;
import io.matoshri.bdayreminder.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/birthday")
@Slf4j
@CrossOrigin(value = "http://localhost:4200")
public class PersonController {

    final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping(value = "/save")
    @ResponseStatus(HttpStatus.CREATED)
    Person saveNewBirthdayPerson(@RequestBody @Valid PersonRequest personRequest) {
        try {
            return CompletableFuture.supplyAsync(() -> service.saveNewBirthdayPerson(personRequest)).get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @GetMapping(value = "/all")
    List<Person> findAll() {
        try {
            return CompletableFuture.supplyAsync(service::findAll).get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @GetMapping(value = "/find-by-name/{personName}")
    List<Person> findAllByPersonName(@PathVariable("personName") String personName) {
        try {
            return CompletableFuture.supplyAsync(() -> service.findAllByPersonName(personName)).get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @GetMapping(value = "/find-by-date/{birthDate}")
    List<Person> findAllByBirthDate(@PathVariable("birthDate") String birthDate) {
        try {
            return CompletableFuture.supplyAsync(() -> service.findAllByBirthDate(birthDate)).get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @GetMapping(value = "/fetch-by-today")
    List<Person> findAllByBirthDate() {
        try {
            return CompletableFuture.supplyAsync(service::findAllTodayBirthdayPersons).get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @GetMapping(value = "/fetch-by-upcoming")
    List<Person> findAllUpcomingBirthdayPersons() {
        try {
            return CompletableFuture.supplyAsync(service::findAllUpcomingBirthdayPersons).get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @GetMapping(value = "/generate-csv")
    @ResponseStatus(HttpStatus.CREATED)
    void generateCSVFile() {
        service.generateCSVFile();
    }

    @GetMapping(value = "/generate-json")
    @ResponseStatus(HttpStatus.CREATED)
    void generateJSONFile() {
        service.generateJSONFile();
    }
}
