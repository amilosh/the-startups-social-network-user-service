package school.faang.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UserWithFollowersDto {
    private Long id;
    private String username;
    private String fileId;
    private String smallFileId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private List<Long> followerIds;

    public UserWithFollowersDto(Long id, String username, String fileId, String smallFileId, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.fileId = fileId;
        this.smallFileId = smallFileId;
        this.createdAt = createdAt;
    }
}
