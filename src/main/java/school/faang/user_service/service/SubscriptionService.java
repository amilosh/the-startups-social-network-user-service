package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;

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

    private List<UserDTO> filterUsers(List<User> users, UserFilterDTO filter) {
        return users.stream()
            .filter(user -> filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern()))
            .filter(user -> filter.getAboutPattern() == null || user.getAboutMe().contains(filter.getAboutPattern()))
            .filter(user -> filter.getEmailPattern() == null || user.getEmail().contains(filter.getEmailPattern()))
            .filter(user -> filter.getExperienceMin() == null || user.getExperience() >= filter.getExperienceMin())
            .filter(user -> filter.getExperienceMax() == null || user.getExperience() <= filter.getExperienceMax())
            .skip(filter.getPage() != null && filter.getPageSize() != null ? filter.getPage() * filter.getPageSize() : 0)
            .limit(filter.getPageSize() != null ? filter.getPageSize() : Long.MAX_VALUE)
            .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail()))
            .collect(Collectors.toList());
    }
}
