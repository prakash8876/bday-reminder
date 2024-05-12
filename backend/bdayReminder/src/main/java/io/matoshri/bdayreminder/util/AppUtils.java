package io.matoshri.bdayreminder.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class AppUtils {

    public final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public final static String[] header = new String[]{"ID", "NAME", "DATE"};
    public final static String CSV = ".csv";
    public final static String JSON = ".json";


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
