package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String username;
    private String aboutMe;
    private String email;
    private List<Long> menteeIds;
    private List<Long> mentorIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
