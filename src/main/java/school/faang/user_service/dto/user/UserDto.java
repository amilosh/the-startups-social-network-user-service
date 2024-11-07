package school.faang.user_service.dto.user;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
    private boolean active;
    private String aboutMe;
    private Integer experience;
    private String createdAt;
    private List<Long> followersIds;
    private List<Long> followeesIds;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
    private List<Long> goalsIds;
    private List<Long> skillsIds;

}
