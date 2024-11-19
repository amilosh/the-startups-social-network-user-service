package school.faang.user_service.model.person.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Status {
    @JsonProperty(index = 16)
    private String status;
    @JsonProperty(index = 17)
    private String admissionDate;
    @JsonProperty(index = 18)
    private String graduationDate;
    @JsonUnwrapped
    private PreviousEducation previousEducation;
}
