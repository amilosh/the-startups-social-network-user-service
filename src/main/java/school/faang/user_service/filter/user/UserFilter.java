package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

@Component
public class UserFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getNamePattern() != null &&
                filter.getEmailPattern() != null ||
                filter.getPhonePattern() != null;
    }

    @Override
    public boolean apply(User user, UserFilterDto filter) {
        boolean matches = true;

        if (filter.getNamePattern() != null) {
            matches &= user.getUsername().matches(filter.getNamePattern());
        }
        if (filter.getAboutPattern() != null) {
            matches &= user.getAboutMe().matches(filter.getAboutPattern());
        }
        if (filter.getEmailPattern() != null) {
            matches &= user.getEmail().matches(filter.getEmailPattern());
        }
        if (filter.getContactPattern() != null) {
            matches &= user.getContacts().stream()
                    .anyMatch(contact -> contact.getContact().matches(filter.getContactPattern()));
        }
        if (filter.getCountryPattern() != null) {
            matches &= user.getCountry().getTitle().matches(filter.getCountryPattern());
        }
        if (filter.getCityPattern() != null) {
            matches &= user.getCity().matches(filter.getCityPattern());
        }
        if (filter.getPhonePattern() != null) {
            matches &= user.getPhone().matches(filter.getPhonePattern());
        }
        if (filter.getSkillPattern() != null) {
            matches &= user.getSkills().stream()
                    .anyMatch(skill -> skill.getTitle().matches(filter.getSkillPattern()));
        }
        if (filter.getExperienceMin() > 0) {
            matches &= user.getExperience() >= filter.getExperienceMin();
        }
        if (filter.getExperienceMax() > 0) {
            matches &= user.getExperience() <= filter.getExperienceMax();
        }

        return matches;
    }
}
