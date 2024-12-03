package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.stream.Stream;

@Component
public class UserFilterName implements Filter<UserDto, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getName() != null;
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> dataStream, UserFilterDto filter) {
        return dataStream.filter(user -> user.getUsername().contains(filter.getName()));
    }
}
