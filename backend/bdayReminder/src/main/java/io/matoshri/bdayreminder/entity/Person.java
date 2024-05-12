package io.matoshri.bdayreminder.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PERSON")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", unique = true, updatable = false)
    private Integer personId;

    @Column(name = "person_name", length = 20, nullable = false)
    private String personName;

    @Column(name = "birth_date", length = 10, nullable = false)
    private String birthDate;

    public String[] forCSV() {
        String[] str = new String[]{this.personId.toString(), this.personName, this.birthDate};
        return str;
    }
}
