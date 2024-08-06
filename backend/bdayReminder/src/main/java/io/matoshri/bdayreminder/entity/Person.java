package io.matoshri.bdayreminder.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.matoshri.bdayreminder.util.AppUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "PERSON_TBL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", unique = true, updatable = false)
    @Getter
    private int personId;

    @Column(name = "person_name", length = 20, nullable = false)
    @Getter
    private String personName;

    @Column(name = "birth_date", length = 10, nullable = false)
    @Getter
    private String birthDate;

    public String[] forCSV() {
        return new String[]{Integer.toString(this.personId), this.personName, this.birthDate};
    }

    @JsonIgnore
    public String getMonthDay() {
        LocalDate localDate = LocalDate.parse(this.birthDate, AppUtils.getFormatter());
        LocalDate ld = LocalDate.of(LocalDate.MIN.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
        return ld.format(AppUtils.getFormatter());
    }

    @Override
    public String toString() {
        return "Person{" +
                "personId=" + personId +
                ", personName='" + personName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;
        return Objects.equals(personName, person.personName) && Objects.equals(birthDate, person.birthDate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(personName);
        result = 31 * result + Objects.hashCode(birthDate);
        return result;
    }
}
