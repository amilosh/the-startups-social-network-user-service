package school.faang.user_service.service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.UserEmailFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserEmailFilterTest {
    final UserEmailFilter filter = new UserEmailFilter();

    @Test
    void testMatches() {
        User user = new User();
        user.setEmail("test@mail");

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setEmailPattern("(.*)@mail");

        assertTrue(filter.apply(user, filterDto));
    }

    @Test
    void testNotMatches() {
        User user = new User();
        user.setEmail("test@gmail");

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setEmailPattern("(.*)@mail");

        assertFalse(filter.apply(user, filterDto));
    }

}
