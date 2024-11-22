package school.faang.user_service.dto.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
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
