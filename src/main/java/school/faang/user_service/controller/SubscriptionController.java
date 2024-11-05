package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorMessage;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.util.ServiceParameters;

@RestController
@RequiredArgsConstructor
@RequestMapping(ServiceParameters.FOLLOWING_SERVICE_URL)
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Transactional
    @PostMapping(ServiceParameters.FOLLOWING_ADD + ServiceParameters.FOLLOWING_PARAMETERS)
    @ResponseStatus(HttpStatus.OK)
    public void followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException(ErrorMessage.M_FOLLOW_YOURSELF);
        }
    }

    @PostMapping(ServiceParameters.FOLLOWING_DELETE + ServiceParameters.FOLLOWING_PARAMETERS)
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException(ErrorMessage.M_UNFOLLOW_YOURSELF);
        }
    }
}