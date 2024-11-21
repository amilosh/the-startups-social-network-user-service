package school.faang.user_service.dto.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PreviousEducation {
    @JsonProperty("degree")
    private String degree;
    @JsonProperty("institution")
    private String institution;
    @JsonProperty("completionYear")
    private int completionYear;
}
