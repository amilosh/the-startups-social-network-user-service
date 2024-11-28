package school.faang.user_service.pojo.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviousEducation {

    private String degree;
    private String institution;
    private int completionYear;

}
