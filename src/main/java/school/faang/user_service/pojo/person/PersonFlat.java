package school.faang.user_service.pojo.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PersonFlat {

    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentID;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String faculty;
    private Integer yearOfStudy;
    private String major;
    @JsonProperty("GPA")
    private Double gpa;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date admissionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date graduationDate;
    private String degree;
    private String institution;
    private int completionYear;
    private Boolean scholarship;
    private String employer;

}
