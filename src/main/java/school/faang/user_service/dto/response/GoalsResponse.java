package school.faang.user_service.dto.response;

import lombok.Data;
import school.faang.user_service.dto.GoalDTO;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoalsResponse {
    private String message;
    private int code;
    private List<GoalDTO> data = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public GoalsResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
