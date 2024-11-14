package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.stream.Stream;

@Component
public class UserFilter implements Filter<UserDto, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getNamePattern() != null || filter.getEmailPattern() != null;
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> dataStream, UserFilterDto filter) {
        return dataStream
                .filter(user -> filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern()))
                .filter(user -> filter.getEmailPattern() == null || user.getEmail().contains(filter.getEmailPattern()));
    }
}
