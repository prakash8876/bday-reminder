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
        LocalDate parse = LocalDate.parse(birthDate, AppUtils.getFormatter());
        String format = parse.format(AppUtils.getFormatter());
        Assertions.assertEquals(birthDate, format);
    }

}
