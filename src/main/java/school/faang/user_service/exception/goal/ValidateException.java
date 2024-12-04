package school.faang.user_service.exception.goal;

import lombok.Data;

@Data
public class ValidateException extends RuntimeException{
    private final String massage;

}
