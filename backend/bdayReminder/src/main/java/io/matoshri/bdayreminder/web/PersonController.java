package io.matoshri.bdayreminder.web;

import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.exception.ApiException;
import io.matoshri.bdayreminder.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;
    private List<Person> list = new ArrayList<>();

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
    @ResponseStatus(HttpStatus.OK)
    List<Person> findAll() {
        try {
            list = CompletableFuture.supplyAsync(service::findAll).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/by/name/{personName}")
    @ResponseStatus(HttpStatus.OK)
    List<Person> findAllByPersonName(@PathVariable("personName") String personName) {
        try {
            list = CompletableFuture.supplyAsync(() -> service.findAllByPersonName(personName)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/by/date/{birthDate}")
    @ResponseStatus(HttpStatus.OK)
    List<Person> findAllByBirthDate(@PathVariable("birthDate") String birthDate) {
        try {
            list = CompletableFuture.supplyAsync(() -> service.findAllByBirthDate(birthDate)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/by/today")
    @ResponseStatus(HttpStatus.OK)
    List<Person> findAllByBirthDate() {
        try {
            list = CompletableFuture.supplyAsync(service::findAllTodayBirthdayPersons).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/by/upcoming")
    @ResponseStatus(HttpStatus.OK)
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
