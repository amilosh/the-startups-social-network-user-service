package school.faang.user_service.dto.project.meet.MeetDto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetDto {
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    private Long projectId;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
