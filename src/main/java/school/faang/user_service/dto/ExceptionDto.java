package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ExceptionDto {
    private int status;
    private HttpStatus error;
    private String description;
}
