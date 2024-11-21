package school.faang.user_service.pojo.person;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"street", "city", "state", "country", "postalCode"})
public class Address {

    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;

}
