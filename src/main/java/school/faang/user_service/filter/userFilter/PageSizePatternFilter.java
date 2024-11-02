package school.faang.user_service.filter.userFilter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class PageSizePatternFilter implements UserFilter {
    private int pattern;

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        this.pattern = filterDto.pageSize();
        return pattern > 0;
    }

    @Override
    public boolean apply(User user) {
        return true;
    }
}
