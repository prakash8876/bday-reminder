package io.matoshri.bdayreminder.entity;

import lombok.Getter;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Getter
public class PersonRequest {
    private int personId;

    @NotNull(message = "Name should not be empty")
    private String personName;

    @NotNull(message = "Birth date should not be empty")
    private String birthDate;

    public PersonRequest(int personId, String personName, String birthDate) {
        this.personId = personId;
        this.personName = personName;
        this.birthDate = birthDate;
    }
}
