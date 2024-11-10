package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
    private String aboutMe;
    private String email;
    private List<Long> menteeIds;
    private List<Long> mentorIds;
}
