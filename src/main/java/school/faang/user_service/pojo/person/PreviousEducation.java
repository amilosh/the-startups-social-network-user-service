package school.faang.user_service.pojo.person;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"degree", "institution", "completionYear"})
public class PreviousEducation {

    private String degree;
    private String institution;
    private int completionYear;

}
