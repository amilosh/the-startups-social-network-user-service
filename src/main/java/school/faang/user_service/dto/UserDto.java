package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private List<Long> menteesId;
    private List<Long> mentorsId;
    private List<Long> skillsId;
}
