package school.faang.user_service.pojo.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Education {

    private String faculty;
    private Integer yearOfStudy;
    private String major;
    private Double gpa;

}
