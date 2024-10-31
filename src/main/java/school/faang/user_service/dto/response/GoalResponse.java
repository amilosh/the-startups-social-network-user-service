package school.faang.user_service.dto.response;

import lombok.Data;
import school.faang.user_service.dto.GoalDTO;

import java.util.List;

@Data
public class GoalResponse {
    private String message;
    private int code;
    private GoalDTO data;
    private List<String> errors;

    public GoalResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
