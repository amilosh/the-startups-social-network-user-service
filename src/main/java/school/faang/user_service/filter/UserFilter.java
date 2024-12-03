package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.stream.Stream;

@Component
public class UserFilter implements Filter<UserDto, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getName() != null || filter.getEmail() != null;
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> dataStream, UserFilterDto filter) {
        return dataStream
                .filter(user -> filter.getName() == null || user.getUsername().contains(filter.getName()))
                .filter(user -> filter.getEmail() == null || user.getEmail().contains(filter.getEmail()));
    }
}
