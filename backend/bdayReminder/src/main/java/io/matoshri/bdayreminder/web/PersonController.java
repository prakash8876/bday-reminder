package io.matoshri.bdayreminder.web;

import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.exception.ApiException;
import io.matoshri.bdayreminder.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/birthday")
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:4200")
public class PersonController {

    private final PersonService service;
    private List<Person> list;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    Person saveNewBirthdayPerson(@RequestBody @Valid Person person) {
        Person person1;
        try {
            person1 = CompletableFuture.supplyAsync(() -> service.saveNewBirthdayPerson(person)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return person1;
    }

    @GetMapping("/all")
    List<Person> findAll() {
        try {
            list = CompletableFuture.supplyAsync(service::findAll).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/find-by-name/{personName}")
    List<Person> findAllByPersonName(@PathVariable("personName") String personName) {
        try {
            list = CompletableFuture.supplyAsync(() -> service.findAllByPersonName(personName)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/find-by-date/{birthDate}")
    List<Person> findAllByBirthDate(@PathVariable("birthDate") String birthDate) {
        try {
            list = CompletableFuture.supplyAsync(() -> service.findAllByBirthDate(birthDate)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/fetch-by-today")
    List<Person> findAllByBirthDate() {
        try {
            list = CompletableFuture.supplyAsync(service::findAllTodayBirthdayPersons).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/fetch-by-upcoming")
    List<Person> findAllUpcomingBirthdayPersons() {
        try {
            list = CompletableFuture.supplyAsync(service::findAllUpcomingBirthdayPersons).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/generate-csv")
    @ResponseStatus(HttpStatus.CREATED)
    void generateCSVFile() {
        service.generateCSVFile();
    }

    @GetMapping("/generate-json")
    @ResponseStatus(HttpStatus.CREATED)
    void generateJSONFile() {
        service.generateJSONFile();
    }
}
