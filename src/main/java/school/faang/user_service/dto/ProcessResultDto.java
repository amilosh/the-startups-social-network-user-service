package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ProcessResultDto {

    private int сountSuccessfullySavedUsers;
    private List<String> errors;
}
