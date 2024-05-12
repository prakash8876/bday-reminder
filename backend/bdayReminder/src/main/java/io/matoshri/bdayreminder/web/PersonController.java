package io.matoshri.bdayreminder.web;

import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.exception.ApiException;
import io.matoshri.bdayreminder.service.PersonService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/by/name/{personName}")
    List<Person> findAllByPersonName(@PathVariable("personName") String personName) {
        try {
            list = CompletableFuture.supplyAsync(() -> service.findAllByPersonName(personName)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/by/date/{birthDate}")
    List<Person> findAllByBirthDate(@PathVariable("birthDate") String birthDate) {
        try {
            list = CompletableFuture.supplyAsync(() -> service.findAllByBirthDate(birthDate)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/by/today")
    List<Person> findAllByBirthDate() {
        try {
            list = CompletableFuture.supplyAsync(service::findAllTodayBirthdayPersons).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/by/upcoming")
    List<Person> findAllUpcomingBirthdayPersons() {
        try {
            list = CompletableFuture.supplyAsync(service::findAllUpcomingBirthdayPersons).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }

    @GetMapping("/generate-csv")
    void generateCSVFile() {
        service.generateCSVFile();
    }

    @GetMapping("/generate-json")
    void generateJSONFile() {
        service.generateJSONFile();
    }
}
