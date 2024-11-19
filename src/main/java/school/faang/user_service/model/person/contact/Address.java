package school.faang.user_service.model.person.contact;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    @JsonProperty(index = 7)
    private String street;
    @JsonProperty(index = 8)
    private String city;
    @JsonProperty(index = 9)
    private String state;
    @JsonProperty(index = 10)
    private String country;
    @JsonProperty(index = 11)
    private String postalCode;
}
