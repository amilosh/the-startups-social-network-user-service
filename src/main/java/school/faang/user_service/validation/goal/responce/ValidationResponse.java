package school.faang.user_service.validation.goal.responce;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationResponse {
    private boolean isValid;
    private List<String> errors = new ArrayList<>();
}
