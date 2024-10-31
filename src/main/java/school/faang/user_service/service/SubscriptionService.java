package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.ExperienceFilter;
import school.faang.user_service.filter.NameFilter;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.filter.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("Пользователь не может подписаться на себя.");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("Подписка уже существует.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("Подписка не существует.");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDTO> getFollowers(Long userId, UserFilterDTO filter) {
        if (filter == null) {
            filter = new UserFilterDTO();
        }
        return filterUsers(subscriptionRepository.findByFolloweeId(userId).collect(Collectors.toList()), filter);
    }

    public boolean subscriptionExists(Long followerId, Long followeeId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public List<UserDTO> filterUsers(List<User> users, UserFilterDTO filter) {
        List<UserFilter> filters = new ArrayList<>();
        if (filter.getNamePattern() != null) {
            filters.add(new NameFilter(filter.getNamePattern()));
        }
        if (filter.getExperienceMin() != null || filter.getExperienceMax() != null) {
            filters.add(new ExperienceFilter(filter.getExperienceMin(), filter.getExperienceMax()));
        }

        return users.stream()
            .filter(user -> filters.stream().allMatch(f -> f.filter(user)))
            .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail()))
            .collect(Collectors.toList());
    }
}
