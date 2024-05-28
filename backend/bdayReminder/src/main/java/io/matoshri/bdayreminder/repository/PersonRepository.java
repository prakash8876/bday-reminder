package io.matoshri.bdayreminder.repository;

import io.matoshri.bdayreminder.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findAllByPersonName(String personName);

    List<Person> findAllByBirthDate(String birthDate);

    boolean existsByPersonName(String personName);

    boolean existsByBirthDate(String birthDate);
}
