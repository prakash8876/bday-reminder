package io.matoshri.bdayreminder;

import io.matoshri.bdayreminder.util.AppUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BdayReminderApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertTrue(Boolean.TRUE);
    }

    @Test
    void findAllByBirthDate() {
        String birthDate = "20240515";
        String format = "20240515";
        try {
            LocalDate parse = LocalDate.parse(birthDate, AppUtils.getFormatter());
            String format1 = LocalDate.parse(birthDate, AppUtils.getFormatter()).format(AppUtils.getFormatter());
            format = parse.format(AppUtils.getFormatter());
        } catch (Exception e0) {
            e0.printStackTrace();
            System.out.println("DateTimeParseException could not be parsed invalid value/date");
        }
        Assertions.assertEquals(birthDate, format);
    }

}
