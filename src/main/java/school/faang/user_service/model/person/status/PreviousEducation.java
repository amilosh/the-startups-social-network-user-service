package school.faang.user_service.model.person.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreviousEducation {
    @JsonProperty(index = 19)
    private String degree;
    @JsonProperty(index = 20)
    private String institution;
    @JsonProperty(index = 21)
    private Integer completionYear;
}
