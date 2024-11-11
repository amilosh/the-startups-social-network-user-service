package school.faang.user_service.service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.UserNameFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserNameFilterTest {
    final UserNameFilter filter = new UserNameFilter();

    @Test
    void testMatches() {
        User user = new User();
        user.setUsername("Vlad");

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("Vl(.*)");

        assertTrue(filter.apply(user, filterDto));
    }

    @Test
    void testNotMatches() {
        User user = new User();
        user.setUsername("Ivan");

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("Vl(.*)");

        assertFalse(filter.apply(user, filterDto));
    }

}
