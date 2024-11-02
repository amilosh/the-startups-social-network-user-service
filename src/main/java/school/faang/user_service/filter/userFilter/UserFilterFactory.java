package school.faang.user_service.filter.userFilter;

import school.faang.user_service.dto.UserFilterDto;

import java.util.ArrayList;
import java.util.List;

public class UserFilterFactory {
    public static List<UserFilter> createFilters(UserFilterDto filterDto) {
        List<UserFilter> filters = new ArrayList<>();

        UserFilter nameFilter = new NamePatternFilter();
        if (nameFilter.isApplicable(filterDto)) {
            filters.add(nameFilter);
        }

        UserFilter emailFilter = new EmailPatternFilter();
        if (emailFilter.isApplicable(filterDto)) {
            filters.add(emailFilter);
        }

        UserFilter pageSizeFilter = new PageSizePatternFilter();
        if (pageSizeFilter.isApplicable(filterDto)) {
            filters.add(pageSizeFilter);
        }

        UserFilter pageNumberFilter = new PagePatternFilter();
        if (pageNumberFilter.isApplicable(filterDto)) {
            filters.add(pageNumberFilter);
        }

        UserFilter aboutFilter = new AboutPatternFilter();
        if (aboutFilter.isApplicable(filterDto)) {
            filters.add(aboutFilter);
        }

        UserFilter countryFilter = new CountryPatternFilter();
        if (countryFilter.isApplicable(filterDto)) {
            filters.add(countryFilter);
        }

        UserFilter cityFilter = new CityPatternFilter();
        if (cityFilter.isApplicable(filterDto)) {
            filters.add(cityFilter);
        }

        UserFilter contactFilter = new ContactPatternFilter();
        if (contactFilter.isApplicable(filterDto)) {
            filters.add(contactFilter);
        }

        UserFilter minExperienceFilter = new MinExperiencePatternFilter();
        if (minExperienceFilter.isApplicable(filterDto)) {
            filters.add(minExperienceFilter);
        }

        UserFilter maxExperienceFilter = new MaxExperiencePatternFilter();
        if (maxExperienceFilter.isApplicable(filterDto)) {
            filters.add(maxExperienceFilter);
        }

        UserFilter phoneNumberFilter = new PhonePatternFilter();
        if (phoneNumberFilter.isApplicable(filterDto)) {
            filters.add(phoneNumberFilter);
        }

        UserFilter skillsFilter = new SkillsPatternFilter();
        if (skillsFilter.isApplicable(filterDto)) {
            filters.add(skillsFilter);
        }

        return filters;
    }
}