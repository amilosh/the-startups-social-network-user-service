package school.faang.user_service.dto.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ContactInfo {
    @Email(message = "Invalid email format")
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;

    private Address address;
}
