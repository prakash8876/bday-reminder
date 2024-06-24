package io.matoshri.bdayreminder.repository;

import io.matoshri.bdayreminder.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findAllByPersonName(String personName);

    List<Person> findAllByBirthDate(String birthDate);

    boolean existsByPersonName(String personName);

    boolean existsByBirthDate(String birthDate);
}
