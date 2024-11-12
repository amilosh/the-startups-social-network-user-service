package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;


    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserMapper userMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userMapper = userMapper;
    }

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        List<UserDto> followersDto = userMapper.toDto(followers); // Используем внедренный маппер
        return filterUsers(followersDto, filter);
    }

    public List<UserDto> filterUsers(List<UserDto> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern()))
                .filter(user -> filter.getEmailPattern() == null || user.getEmail().contains(filter.getEmailPattern()))
                .collect(Collectors.toList());
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        List<User> following = subscriptionRepository.findByFolloweeId(followeeId).toList();
        List<UserDto> followingDto = userMapper.toDto(following); // Используем маппер для преобразования
        return filterUsers(followingDto, filter);
    }

    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

}
