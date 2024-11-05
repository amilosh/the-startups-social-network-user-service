package school.faang.user_service.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class ServiceMethods {

    public static String getTimeIsoOffsetDateTime() {
        return new Timestamp(System.currentTimeMillis())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm-ss.SSS"));
    }
}