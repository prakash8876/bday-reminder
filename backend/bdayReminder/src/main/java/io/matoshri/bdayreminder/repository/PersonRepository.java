package io.matoshri.bdayreminder.repository;

import io.matoshri.bdayreminder.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<PersonEntity, Integer> {

    List<PersonEntity> findAllByPersonName(String personName);

    List<PersonEntity> findAllByBirthDate(String birthDate);

    boolean existsByPersonName(String personName);

    boolean existsByBirthDate(String birthDate);
}
