package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecommendationReceivedEvent {
    private long id;
    private long authorId;
    private long receiverId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recommendationTime;
}
