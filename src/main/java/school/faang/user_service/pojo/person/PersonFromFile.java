package school.faang.user_service.pojo.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PersonFromFile {

    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentID;
    private ContactInfo contactInfo;
    private Education education;
    private String status;
    private Date admissionDate;
    private Date graduationDate;
    private List<PreviousEducation> previousEducation;
    private Boolean scholarship;
    private String employer;

}
