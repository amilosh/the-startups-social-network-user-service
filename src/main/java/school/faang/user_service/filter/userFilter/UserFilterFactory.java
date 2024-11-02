package school.faang.user_service.filter.userFilter;

import school.faang.user_service.dto.UserFilterDto;

import java.util.ArrayList;
import java.util.List;

public class UserFilterFactory {
    public static List<UserFilter> createFilters(UserFilterDto filterDto) {
        List<UserFilter> filters = new ArrayList<>();

        if (filterDto.namePattern() != null) {
            filters.add(new NamePatternFilter(filterDto.namePattern()));
        }

        if (filterDto.emailPattern() != null) {
            filters.add(new EmailPatternFilter(filterDto.emailPattern()));
        }

        if (filterDto.pageSize() != null) {
            filters.add(new PageSizePatternFilter());
        }

        if (filterDto.page() != null) {
            filters.add(new PagePatternFilter());
        }

        if (filterDto.aboutPattern() != null) {
            filters.add(new AboutPatternFilter(filterDto.aboutPattern()));
        }

        if (filterDto.countryPattern() != null) {
            filters.add(new CountryPatternFilter(filterDto.countryPattern()));
        }

        if (filterDto.cityPattern() != null) {
            filters.add(new CityPatternFilter(filterDto.cityPattern()));
        }

        if (filterDto.contactPattern() != null) {
            filters.add(new ContactPatternFilter(filterDto.contactPattern()));
        }

        if (filterDto.experienceMin() != null) {
            filters.add(new MinExperiencePatternFilter(filterDto.experienceMin()));
        }

        if (filterDto.experienceMax() != null) {
            filters.add(new MaxExperiencePatternFilter(filterDto.experienceMax()));
        }

        if (filterDto.phonePattern() != null) {
            filters.add(new PhonePatternFilter(filterDto.phonePattern()));
        }

        if (filterDto.skillPattern() != null) {
            filters.add(new SkillsPatternFilter(filterDto.skillPattern()));
        }

        return filters;
    }
}