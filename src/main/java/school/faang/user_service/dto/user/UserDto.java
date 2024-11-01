package school.faang.user_service.dto.user;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class UserDto {
    private long id;
    private String username;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;

}
