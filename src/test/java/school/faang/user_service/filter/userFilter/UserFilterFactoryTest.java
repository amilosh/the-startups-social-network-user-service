package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserFilterFactoryTest {

    UserFilterDto userFilter = UserFilterDto.builder()
            .aboutPattern("something to filter")
            .build();

    @Test
    public void testIfCreatesNeededFilters() {
        // Act
        List<UserFilter> filters = UserFilterFactory.createFilters(userFilter);

        // Assert
        assertEquals(1, filters.size());
    }

    @Test
    public void testIfFiltersAreCorrect() {
        // Act
        List<UserFilter> filters = UserFilterFactory.createFilters(userFilter);

        // Assert
        assertEquals(AboutPatternFilter.class, filters.get(0).getClass());
    }

    @Test
    public void testIfFiltersUsers() {
        // Arrange
        User user1 = new User();
        user1.setAboutMe("something to filter. Something else. Should return this user.");
        User user2 = new User();
        user2.setAboutMe("something NOT to filter. This user shouldn't be returned.");
        List<User> users = List.of(user1, user2);


        // Act
        List<UserFilter> filters = UserFilterFactory.createFilters(userFilter);
        List<UserFilter> applicableFilters = filters.stream()
                .filter(filter -> filter.isApplicable(userFilter))
                .toList();

        List<User> filteredUsers = users.stream()
                .filter(user -> applicableFilters.stream().allMatch(filter -> filter.apply(user)))
                .toList();

        // Assert
        assertEquals(1, filteredUsers.size());
        assertEquals(user1, filteredUsers.get(0));
    }
}
