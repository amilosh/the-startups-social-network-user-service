package school.faang.user_service.pojo.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
        "faculty",
        "yearOfStudy",
        "major",
        "GPA"
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Education {

    private String faculty;
    private Integer yearOfStudy;
    private String major;
    @JsonProperty("GPA")
    private Double gpa;

}
