package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.model.event.FollowerEvent;
import school.faang.user_service.publisher.FollowerEventPublisher;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class FollowerEventController {

    private final FollowerEventPublisher followerEventPublisher;

    @PostMapping("/user/{followerId}/follow/{followedUserId}")
    public ResponseEntity<String> followUser(@PathVariable Long followerId, @PathVariable Long followedUserId) {
        try {
            FollowerEvent followerEvent = new FollowerEvent();
            followerEvent.setFollowerId(followerId);
            followerEvent.setFollowedUserId(followedUserId);
            followerEvent.setFollowedAt(LocalDateTime.now());

            followerEventPublisher.publish(followerEvent);

            return ResponseEntity.ok("Successfully followed the user.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to follow the user: " + e.getMessage());
        }
    }

    @PostMapping("/user/{followerId}/follow-project/{followedProjectId}")
    public ResponseEntity<String> followProject(@PathVariable Long followerId, @PathVariable Long followedProjectId) {
        try {
            FollowerEvent followerEvent = new FollowerEvent();
            followerEvent.setFollowerId(followerId);
            followerEvent.setFollowedProjectId(followedProjectId);
            followerEvent.setFollowedAt(LocalDateTime.now());

            followerEventPublisher.publish(followerEvent);

            return ResponseEntity.ok("Successfully followed the project.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to follow the project: " + e.getMessage());
        }
    }
}