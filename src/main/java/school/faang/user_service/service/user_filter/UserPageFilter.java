package school.faang.user_service.service.user_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPageFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        int pageNumb = filter.getPage();
        int pageSize = filter.getPageSize();
        long usersToSkip = ((long) alignPageNumber(pageNumb) * pageSize);

        return users
                .skip(usersToSkip)
                .limit(pageSize);
    }

    private int alignPageNumber(int pageNumb) {
        return pageNumb - 1;
    }
}
