package school.faang.user_service.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.awt.*;


@Data
public class MentorshipOfferedEvent {
    private long authorId;
    private long mentorId;
}
