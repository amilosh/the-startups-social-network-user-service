package school.faang.user_service.model.person.contact;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactInfo {
    @JsonProperty(index = 5)
    private String email;
    @JsonProperty(index = 6)
    private String phone;
    @JsonUnwrapped
    private Address address;
}
