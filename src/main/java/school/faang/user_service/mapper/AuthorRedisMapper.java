package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.AuthorRedisDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorRedisMapper {
    List<AuthorRedisDto> toAuthorRedisDtos (List<User>users);
}
