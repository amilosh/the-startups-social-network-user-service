package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UserFilterExperienceMoreThanOrEqual implements UserFilter {
    @Override
    public boolean apply(User user, UserFilterDto userFilterDto) {
        return userFilterDto.getExperienceMin() == null || user.getExperience() >= userFilterDto.getExperienceMin();
    }
}