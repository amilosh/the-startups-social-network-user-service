package school.faang.user_service.model.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Education {
    @JsonProperty(index = 12)
    private String faculty;
    @JsonProperty(index = 13)
    private Integer yearOfStudy;
    @JsonProperty(index = 14)
    private String major;
    @JsonProperty(value = "GPA", index = 15)
    private Float gpa;
}
