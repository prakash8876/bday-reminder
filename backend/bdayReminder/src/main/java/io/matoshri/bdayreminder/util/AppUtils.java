package io.matoshri.bdayreminder.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class AppUtils {

    private AppUtils() {}

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter monthDay = DateTimeFormatter.ofPattern("MM/dd");
    private static final String[] HEADER = {"ID", "NAME", "DATE"};
    private static final String CSV = ".csv";
    private static final String JSON = ".json";

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    public static DateTimeFormatter getMonthDay() {
        return monthDay;
    }

    public static String[] getHeader() {
        return HEADER;
    }

    public static String getCSVType() { return CSV; }
    public static String getJSONType() { return JSON; }


    public static Path getFilePath(String type) {
        File file = null;
        try {
            File path = new File("src/main/resources/dummy/data" + type);
            path.createNewFile();
            file = path;
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return Path.of(file.toURI());
    }
}
