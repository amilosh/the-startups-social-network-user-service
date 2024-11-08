package school.faang.user_service.mapper;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class DateFormatter {
    public static LocalDateTime stringToLocalDateTime(String dateTimeStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = dateFormat.parse(dateTimeStr);
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            log.error("Error parsing date: ", e);
            throw new IllegalArgumentException(e);
        }
    }
}
