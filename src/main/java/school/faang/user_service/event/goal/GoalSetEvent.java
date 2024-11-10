package school.faang.user_service.event.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalSetEvent {
    private Long userId;
    private Long goalId;

    @Override
    public String toString() {
        return "GoalSetEvent{" +
                "userId=" + userId +
                ", goalId=" + goalId +
                '}';
    }
}
