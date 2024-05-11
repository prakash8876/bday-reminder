package io.matoshri.bdayreminder.service;

import io.matoshri.bdayreminder.entity.Person;

import java.util.List;

public interface PersonService {

    Person saveNewBirthdayPerson(Person person);

    List<Person> findAll();
    List<Person> findAllByPersonName(String personName);
    List<Person> findAllByBirthDate(String birthDate);
    List<Person> findAllTodayBirthdayPersons();
    List<Person> findAllUpcomingBirthdayPersons();

    void generateCSVFile();
    void generateJSONFile();
}
