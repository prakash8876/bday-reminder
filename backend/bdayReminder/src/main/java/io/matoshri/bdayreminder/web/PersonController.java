package io.matoshri.bdayreminder.web;

import io.matoshri.bdayreminder.entity.Person;
import io.matoshri.bdayreminder.exception.ApiException;
import io.matoshri.bdayreminder.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

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

    @GetMapping("/app")
    List<Person> findAll() {
        List<Person> list;
        try {
            list = CompletableFuture.supplyAsync(() -> service.findAll()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiException(e);
        }
        return list;
    }
}
