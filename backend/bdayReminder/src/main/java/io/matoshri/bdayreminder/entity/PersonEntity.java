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
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERSON_ID", unique = true, updatable = false)
    @Getter
    private int personId;

    @Column(name = "PERSON_NAME", length = 20, nullable = false)
    @Getter
    private String personName;

    @Column(name = "BIRTH_DATE", length = 10, nullable = false)
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
                "Id=" + personId +
                ", Name='" + personName + '\'' +
                ", birth Date='" + birthDate + '\'' +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonEntity)) return false;

        PersonEntity person = (PersonEntity) o;
        return Objects.equals(personName, person.personName) && Objects.equals(birthDate, person.birthDate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(personName);
        result = 31 * result + Objects.hashCode(birthDate);
        return result;
    }
}
