package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public void followUser(Long followerId, Long followeeId) throws DataValidationException {
        Boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (exists) {
            throw new DataValidationException("The subscription already exists");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(Long followerId, Long followeeId) throws DataValidationException {
        Boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (!exists) {
            throw new DataValidationException("The subscription doesn't exist already");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(Long followeeId, UserFilterDto filter) {

        if (!subscriptionRepository.existsById(followeeId)) {
            throw new NoSuchElementException("Cannot find followee by id " + followeeId);
        }

        return subscriptionRepository.findByFolloweeId(followeeId)
                .filter(user -> filterUser(filter, user))
                .map(userMapper::toDto)
                .toList();
    }

    private Boolean filterUser(UserFilterDto filter, User user) {
        /**
         * Filter user by {@link filter}
         *
         * returns true, if there's at least one match with the filter
         */

        if (user.getUsername() != null && filter.getNamePattern() != null) {
            String username = user.getUsername().toLowerCase();
            String namePattern = filter.getNamePattern().toLowerCase();

            if (username.matches(".*" + namePattern + ".*")) {
                return true;
            }
        }

        if (user.getAboutMe() != null && filter.getAboutMePattern() != null) {
            String aboutUser = user.getAboutMe().toLowerCase();
            String aboutMePattern = filter.getAboutMePattern().toLowerCase();

            if (aboutUser.matches(".*" + aboutMePattern + ".*")) {
                return true;
            }
        }

        if (user.getEmail() != null && filter.getEmailPattern() != null) {
            String userEmail = user.getEmail().toLowerCase();
            String emailPattern = filter.getEmailPattern().toLowerCase();

            if (userEmail.matches(".*" + emailPattern + ".*")) {
                return true;
            }
        }

        if (user.getCountry() != null && filter.getCountryPattern() != null) {
            String userCountry = user.getCountry().getTitle().toLowerCase();
            String userCountryPattern = filter.getCountryPattern().toLowerCase();

            if (userCountry.matches(".*" + userCountryPattern + ".*")) {
                return true;
            }
        }

        if (user.getCity() != null && filter.getCityPattern() != null) {
            String userCity = user.getCity().toLowerCase();
            String userCityPattern = filter.getCityPattern().toLowerCase();

            if (userCity.matches(".*" + userCityPattern + ".*")) {
                return true;
            }
        }

        if (user.getPhone() != null && filter.getPhonePattern() != null) {
            String userPhone = user.getPhone().toLowerCase();
            String userPhonePattern = filter.getPhonePattern().toLowerCase();

            if (userPhone.matches(".*" + userPhonePattern + ".*")) {
                return true;
            }
        }

        if (user.getSkills() != null && filter.getSkillPattern() != null) {
            String skillPattern = filter.getSkillPattern().toLowerCase();

            for (Skill userSkill : user.getSkills()) {
                String userSkillTitle = userSkill.getTitle().toLowerCase();

                if (userSkillTitle.matches(".*" + skillPattern + ".*")) {
                    return true;
                }
            }
        }

        if (user.getExperience() != null) {
            if (filter.getExperienceMax() != null && filter.getExperienceMin() == null) {
                if (user.getExperience() <= filter.getExperienceMax()) {
                    return true;
                }
            } else if (filter.getExperienceMin() != null && filter.getExperienceMax() == null) {
                if (user.getExperience() >= filter.getExperienceMin()) {
                    return true;
                }
            } else if (filter.getExperienceMax() != null && filter.getExperienceMin() != null) {
                if (user.getExperience() >= filter.getExperienceMin() &&
                        user.getExperience() <= filter.getExperienceMax()) {
                    return true;
                }
            }
        }

        return false;
    }


}
