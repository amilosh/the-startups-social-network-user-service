package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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
import school.faang.user_service.utilities.UrlServiceParameters;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlServiceParameters.FOLLOWING_SERVICE_URL)
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Transactional
    @PostMapping(UrlServiceParameters.FOLLOWING_ADD + UrlServiceParameters.FOLLOWING_PARAMETERS)
    @ResponseStatus(HttpStatus.OK)
    public void followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException(ErrorMessages.M_FOLLOW_YOURSELF);
        }
    }

    @Transactional
    @PostMapping(UrlServiceParameters.FOLLOWING_DELETE + UrlServiceParameters.FOLLOWING_PARAMETERS)
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException(ErrorMessages.M_UNFOLLOW_YOURSELF);
        }
    }

    @Transactional
    @GetMapping(UrlServiceParameters.FOLLOWING_FILTER + "{followeeId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping(UrlServiceParameters.FOLLOWING_COUNT + "{followerId}")
    @ResponseStatus(HttpStatus.OK)
    public int getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}