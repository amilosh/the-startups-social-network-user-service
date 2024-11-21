package school.faang.user_service.dto.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Education {
    @JsonProperty("faculty")
    private String faculty;
    @JsonProperty("yearOfStudy")
    private int yearOfStudy;
    @JsonProperty("major")
    private String major;
    @JsonProperty("GPA")
    private double GPA;
}
