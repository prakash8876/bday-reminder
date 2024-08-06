package io.matoshri.bdayreminder.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@Slf4j
public class AppUtils {

    public static final String DEFAULT_DATE = "20001231";

    private AppUtils() {
    }

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter monthDay = DateTimeFormatter.ofPattern("MM/dd");
    private static final String[] HEADER = {"ID", "NAME", "DATE"};
    public static final String CSV = ".csv";
    public static final String JSON = ".json";

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    public static DateTimeFormatter getMonthDay() {
        return monthDay;
    }

    public static String[] getHeader() {
        return HEADER;
    }

    public static String getCSVType() {
        return CSV;
    }

    public static String getJSONType() {
        return JSON;
    }


    public static Path getFilePath(String type) {
        Path path = null;
        try {
            String date = LocalDate.now().format(getFormatter());
            path = Path.of("src/main/resources/dummy/", date + type);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (Exception e) {
            log.error("Exception: {}", ExceptionUtils.getMessage(e), e);
        }
        return path;
    }

    public static String validateDate(String birthDate) {
        Optional<String> date = Optional.empty();
        LocalDateTime localDateTime = LocalDateTime.now();
        if (StringUtils.isNotEmpty(birthDate)) {
            LocalDate localDate = LocalDate.parse(birthDate, getFormatter());
            if (!localDate.isAfter(localDateTime.toLocalDate()))
                date = Optional.of(localDate.format(getFormatter()));
        }
        return date.orElse(DEFAULT_DATE);
    }
}
