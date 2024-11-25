package school.faang.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UserWithFollowersDto {
    private Long userId;
    private String username;
    private String fileId;
    private String smallFileId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime postCreatedAt;
    private List<Long> followerIds;

    public UserWithFollowersDto(Long userId, String username, String fileId, String smallFileId, LocalDateTime postCreatedAt) {
        this.userId = userId;
        this.username = username;
        this.fileId = fileId;
        this.smallFileId = smallFileId;
        this.postCreatedAt = postCreatedAt;
    }
}
