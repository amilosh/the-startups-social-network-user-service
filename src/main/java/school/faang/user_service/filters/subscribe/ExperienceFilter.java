package school.faang.user_service.filters.subscribe;

import school.faang.user_service.entity.User;
import school.faang.user_service.repository.filter.UserFilter;

public class ExperienceFilter implements UserFilter {
    private final Integer experienceMin;
    private final Integer experienceMax;

    public ExperienceFilter(Integer experienceMin, Integer experienceMax) {
        this.experienceMin = experienceMin;
        this.experienceMax = experienceMax;
    }

    @Override
    public boolean filter(User user) {
        boolean isMinValid = experienceMin == null || user.getExperience() >= experienceMin;
        boolean isMaxValid = experienceMax == null || user.getExperience() <= experienceMax;
        return isMinValid && isMaxValid;
    }
}
