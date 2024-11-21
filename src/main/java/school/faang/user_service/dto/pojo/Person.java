package school.faang.user_service.dto.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;
@Data
public class Person {
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("yearOfBirth")
    private int yearOfBirth;
    @JsonProperty("group")
    private String group;
    @JsonProperty("studentID")
    private String studentID;

    private ContactInfo contactInfo;

    private Education education;
    @JsonProperty("status")
    private String status;
    @JsonProperty("admissionDate")
    private LocalDate admissionDate;
    @JsonProperty("graduationDate")
    private LocalDate graduationDate;
    @JsonIgnore
    private List<PreviousEducation> previousEducation;
    @JsonProperty("scholarship")
    private boolean scholarship;
    @JsonProperty("employer")
    private String employer;
    private String aboutMe = createAboutMe();
    private String name = concatFirstAndLastName();


    private String concatFirstAndLastName() {
        return getFirstName() + getLastName();
    }

    private String createAboutMe() {
        StringBuilder aboutMe = new StringBuilder();
        if (getContactInfo().getAddress().getState() != null
                && !getContactInfo().getAddress().getState().isEmpty()) {
            aboutMe.append(getContactInfo().getAddress().getState()).append(" ");
        }
        aboutMe.append(getEducation().getFaculty()).append(" ")
                .append(getEducation().getYearOfStudy()).append(" ")
                .append(getEducation().getMajor());
        if (getEmployer() != null && !getEmployer().isEmpty()) {
            aboutMe.append(" at ").append(getEmployer());
        }
        return aboutMe.toString();
    }
}
