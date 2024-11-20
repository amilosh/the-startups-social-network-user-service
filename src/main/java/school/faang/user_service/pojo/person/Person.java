package school.faang.user_service.pojo.person;

import com.json.student.ContactInfo;
import com.json.student.Education;
import com.json.student.PreviousEducation;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {
    @CsvBindByName(column = "Name")
    private String firstName;
    @CsvBindByName(column = "Name")
    private String lastName;
    @CsvBindByName(column = "Name")
    private Integer yearOfBirth;
    @CsvBindByName(column = "Name")
    private String group;
    @CsvBindByName(column = "studentID")
    private String studentID;
    private ContactInfo contactInfo;
    private Education education;
    @CsvBindByName(column = "Name")
    private String status;
    @CsvBindByName(column = "Name")
    private Date admissionDate;
    @CsvBindByName(column = "Name")
    private Date graduationDate;
    private List<PreviousEducation> previousEducation;
    @CsvBindByName(column = "Name")
    private Boolean scholarship;
    @CsvBindByName(column = "Name")
    private String employer;

}
