package school.faang.user_service.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Person {

    private String firstName;

    private String lastName;

    private int yearOfBirth;

    private String group;

    private String studentID;

    private ContactInfo contactInfo;

    private Education education;

    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    private List<PreviousEducation> previousEducation;

    private boolean scholarship;

    private String employer;

}
