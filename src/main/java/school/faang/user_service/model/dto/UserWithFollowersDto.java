package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserWithFollowersDto {
    private Long id;
    private String username;
    private String fileId;
    private String smallFileId;
    private List<Long> followerIds;

    public UserWithFollowersDto(Long id, String username, String fileId, String smallFileId) {
        this.id = id;
        this.username = username;
        this.fileId = fileId;
        this.smallFileId = smallFileId;
    }
}
