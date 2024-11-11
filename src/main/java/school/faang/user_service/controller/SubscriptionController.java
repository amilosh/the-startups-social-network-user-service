package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorMessages;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.FOLLOWING_SERVICE_URL)
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(UrlUtils.FOLLOWING_ADD + UrlUtils.FOLLOWING_PARAMETERS)
    public void followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException(ErrorMessages.M_FOLLOW_YOURSELF);
        }
    }

    @PostMapping(UrlUtils.FOLLOWING_DELETE + UrlUtils.FOLLOWING_PARAMETERS)
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException(ErrorMessages.M_UNFOLLOW_YOURSELF);
        }
    }

    @GetMapping(UrlUtils.FOLLOWING_FILTER + "{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping(UrlUtils.FOLLOWING_COUNT + "{followerId}")
    public int getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}