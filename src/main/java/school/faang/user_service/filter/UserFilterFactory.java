package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;

import java.util.ArrayList;
import java.util.List;

public class UserFilterFactory {
    public static List<SubscriptionFilter> createFilters(UserFilterDto filterDto) {
        List<SubscriptionFilter> filters = new ArrayList<>();

        SubscriptionFilter nameFilter = new NamePatternFilter();
        if (nameFilter.isApplicable(filterDto)) {
            filters.add(nameFilter);
        }

        SubscriptionFilter emailFilter = new EmailPatternFilter();
        if (emailFilter.isApplicable(filterDto)) {
            filters.add(emailFilter);
        }

        SubscriptionFilter pageSizeFilter = new PageSizePatternFilter();
        if (pageSizeFilter.isApplicable(filterDto)) {
            filters.add(pageSizeFilter);
        }

        SubscriptionFilter pageNumberFilter = new PagePatternFilter();
        if (pageNumberFilter.isApplicable(filterDto)) {
            filters.add(pageNumberFilter);
        }

        SubscriptionFilter aboutFilter = new AboutPatternFilter();
        if (aboutFilter.isApplicable(filterDto)) {
            filters.add(aboutFilter);
        }

        SubscriptionFilter countryFilter = new CountryPatternFilter();
        if (countryFilter.isApplicable(filterDto)) {
            filters.add(countryFilter);
        }

        SubscriptionFilter cityFilter = new CityPatternFilter();
        if (cityFilter.isApplicable(filterDto)) {
            filters.add(cityFilter);
        }

        SubscriptionFilter contactFilter = new ContactPatternFilter();
        if (contactFilter.isApplicable(filterDto)) {
            filters.add(contactFilter);
        }

        SubscriptionFilter minExperienceFilter = new MinExperiencePatternFilter();
        if (minExperienceFilter.isApplicable(filterDto)) {
            filters.add(minExperienceFilter);
        }

        SubscriptionFilter maxExperienceFilter = new MaxExperiencePatternFilter();
        if (maxExperienceFilter.isApplicable(filterDto)) {
            filters.add(maxExperienceFilter);
        }

        SubscriptionFilter phoneNumberFilter = new PhonePatternFilter();
        if (phoneNumberFilter.isApplicable(filterDto)) {
            filters.add(phoneNumberFilter);
        }

        SubscriptionFilter skillsFilter = new SkillsPatternFilter();
        if (skillsFilter.isApplicable(filterDto)) {
            filters.add(skillsFilter);
        }

        return filters;
    }
}