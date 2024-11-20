package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ExceptionDto {
    private int statusCode;
    private HttpStatus status;
    private String description;
    private List<String> errorMessages;
}
