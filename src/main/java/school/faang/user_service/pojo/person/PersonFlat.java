package school.faang.user_service.pojo.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PersonFlat {

    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("yearOfBirth")
    private Integer yearOfBirth;
    @JsonProperty("group")
    private String group;
    @JsonProperty("studentID")
    private String studentID;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("street")
    private String street;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state")
    private String state;
    @JsonProperty("country")
    private String country;
    @JsonProperty("postalCode")
    private String postalCode;
    @JsonProperty("faculty")
    private String faculty;
    @JsonProperty("yearOfStudy")
    private Integer yearOfStudy;
    @JsonProperty("major")
    private String major;
    @JsonProperty("GPA")
    private Double gpa;
    @JsonProperty("status")
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date admissionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date graduationDate;
    @JsonProperty("degree")
    private String degree;
    @JsonProperty("institution")
    private String institution;
    @JsonProperty("completionYear")
    private int completionYear;
    @JsonProperty("scholarship")
    private Boolean scholarship;
    @JsonProperty("employer")
    private String employer;
}
