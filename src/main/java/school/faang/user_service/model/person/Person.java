package school.faang.user_service.model.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.person.contact.ContactInfo;
import school.faang.user_service.model.person.status.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {
    @JsonProperty(index = 0)
    @NotBlank
    private String firstName;
    @NotBlank
    @JsonProperty(index = 1)
    private String lastName;
    @JsonProperty(index = 2)
    private Integer yearOfBirth;
    @JsonProperty(index = 3)
    private String group;
    @JsonProperty(value = "studentID", index = 4)
    private Long studentId;
    @JsonUnwrapped
    private ContactInfo contactInfo;
    @JsonUnwrapped
    private Education education;
    @JsonUnwrapped
    private Status status;
    @JsonProperty(index = 22)
    private Boolean scholarship;
    @JsonProperty(index = 23)
    private String employer;
}
