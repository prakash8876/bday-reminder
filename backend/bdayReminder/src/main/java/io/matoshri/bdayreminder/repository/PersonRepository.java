package io.matoshri.bdayreminder.repository;

import io.matoshri.bdayreminder.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Integer> {

    List<PersonEntity> findAllByPersonName(String personName);

    List<PersonEntity> findAllByBirthDate(String birthDate);

    boolean existsByPersonName(String personName);

    boolean existsByBirthDate(String birthDate);
}
