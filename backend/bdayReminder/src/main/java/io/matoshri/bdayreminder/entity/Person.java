package io.matoshri.bdayreminder.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.matoshri.bdayreminder.util.AppUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PERSON_TBL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", unique = true, updatable = false)
    private int personId;

    @Column(name = "person_name", length = 20, nullable = false)
    private String personName;

    @Column(name = "birth_date", length = 10, nullable = false)
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
}
