package school.faang.user_service.pojo.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactInfo {

    private String email;
    private String phone;
    private Address address;

}
