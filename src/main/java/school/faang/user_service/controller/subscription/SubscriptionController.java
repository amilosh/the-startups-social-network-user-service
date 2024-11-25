package school.faang.user_service.controller.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscription Controller", description = "Controller for managing user subscriptions")
@ApiResponse(responseCode = "201", description = "User followed successfully")
@ApiResponse(responseCode = "204", description = "User unfollowed successfully")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Follow a user",
            description = "Subscribe one user to follow another"
    )
    @PostMapping("/{followerId}/follow/{followeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(
            @PathVariable @NotNull(message = "Follower ID should not be null") Long followerId,
            @PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @Operation(
            summary = "Unfollow a user",
            description = "Unsubscribe one user from following another"
    )
    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(
            @PathVariable @NotNull(message = "Follower ID should not be null") Long followerId,
            @PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @Operation(
            summary = "Get followers of a user",
            description = "Retrieve the list of followers for a specific user with optional filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Followers retrieved successfully")
            }
    )
    @GetMapping("/{followeeId}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowers(
            @PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId,
            @Valid @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(
            summary = "Get users a user is following",
            description = "Retrieve the list of users that a specific user is following with optional filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Following users retrieved successfully")
            }
    )
    @GetMapping("/{followerId}/following")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowing(
            @PathVariable @NotNull(message = "Follower ID should not be null") Long followerId,
            @Valid @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @Operation(
            summary = "Get followers count",
            description = "Retrieve the total number of followers for a specific user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Followers count retrieved successfully")
            }
    )
    @GetMapping("/{followeeId}/followers/count")
    @ResponseStatus(HttpStatus.OK)
    public int getFollowersCount(@PathVariable @NotNull(message = "Followee ID should not be null") Long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(
            summary = "Get following count",
            description = "Retrieve the total number of users that a specific user is following",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Following count retrieved successfully")
            }
    )
    @GetMapping("/{followerId}/following/count")
    @ResponseStatus(HttpStatus.OK)
    public int getFollowingCount(@PathVariable @NotNull(message = "Follower ID should not be null") Long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
