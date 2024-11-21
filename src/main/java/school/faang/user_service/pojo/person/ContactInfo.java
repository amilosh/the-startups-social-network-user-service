package school.faang.user_service.pojo.person;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"email", "phone", "address"})
public class ContactInfo {

    private String email;
    private String phone;
    private Address address;

}
