package school.faang.user_service.service.subscription;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filter.SubscriptionRequestFilter;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUserMapper userMapper;
    private final List<SubscriptionRequestFilter> subscriptionRequestFilters;

    public SubscriptionRequestDto followUser(Long followerId, Long followeeId) {
        Boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (exists) {
            throw new DataValidationException("The subscription already exists");
        }
        subscriptionRepository.followUser(followerId, followeeId);

        SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto();

        subscriptionRequestDto.setFollowerId(followerId);
        subscriptionRequestDto.setFolloweeId(followeeId);

        return subscriptionRequestDto;
    }

    public SubscriptionRequestDto unfollowUser(Long followerId, Long followeeId) {
        Boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!exists) {
            throw new DataValidationException("The subscription doesn't exist already");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);

        SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto();

        subscriptionRequestDto.setFollowerId(followerId);
        subscriptionRequestDto.setFolloweeId(followeeId);

        return subscriptionRequestDto;
    }

    @Transactional
    public List<SubscriptionUserDto> getFollowers(Long followeeId) {
        if (!subscriptionRepository.existsById(followeeId)) {
            throw new NoSuchElementException("Cannot find followee by id " + followeeId);
        }

        return subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toDto)
                .toList();
    }

    public List<SubscriptionUserDto> getFilteredFollowers(Long followeeId, SubscriptionUserFilterDto filter) {

        if (!subscriptionRepository.existsById(followeeId)) {
            throw new NoSuchElementException("Cannot find followee by id " + followeeId);
        }

        return subscriptionRepository.findByFolloweeId(followeeId)
                .filter(followee -> filterUser(filter, followee))
                .map(userMapper::toDto)
                .toList();
    }

    public Integer getFollowersCount(Long followerId) {
        if (!subscriptionRepository.existsById(followerId)) {
            throw new NoSuchElementException("Cannot find follower by id " + followerId);
        }

        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    public List<SubscriptionUserDto> getFollowing(Long followeeId, SubscriptionUserFilterDto subscriptionUserFilterDto) {
        if (!subscriptionRepository.existsById(followeeId)) {
            throw new NoSuchElementException("Cannot find followee by id " + followeeId);
        }

        return subscriptionRepository.findByFolloweeId(followeeId)
                .filter(followee -> filterUser(subscriptionUserFilterDto, followee))
                .map(userMapper::toDto)
                .toList();
    }

    public Integer getFollowingCount(Long followerId) {
        if (!subscriptionRepository.existsById(followerId)) {
            throw new NoSuchElementException("Cannot find followee by id " + followerId);
        }

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private Boolean filterUser(SubscriptionUserFilterDto userFilter, User user) {
        /**
         * Filter user by {@link filter}
         *
         * returns true, if there's at least one match with the filter
         */

        // iterate all filters
        for (SubscriptionRequestFilter subscriptionRequestFilter: subscriptionRequestFilters) {
            // if this filter is specified in request filter and a user passed this filter
            if (subscriptionRequestFilter.isSpecifiedIn(userFilter) &&
                    subscriptionRequestFilter.applyFilter(userFilter, user)) {
                return true;
            }
        }

        return false;
    }
}
