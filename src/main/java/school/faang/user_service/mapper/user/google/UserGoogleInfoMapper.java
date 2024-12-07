package school.faang.user_service.mapper.user.google;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserGoogleInfoDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserGoogleInfo;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserGoogleInfoMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "objectToLong")
    @Mapping(source = "at_hash", target = "atHash", qualifiedByName = "objectToString")
    @Mapping(source = "sub", target = "sub", qualifiedByName = "objectToString")
    @Mapping(source = "email_verified", target = "emailVerified", qualifiedByName = "objectToBoolean")
    @Mapping(source = "iss", target = "iss", qualifiedByName = "objectToString")
    @Mapping(source = "given_name", target = "givenName", qualifiedByName = "objectToString")
    @Mapping(source = "nonce", target = "nonce", qualifiedByName = "objectToString")
    @Mapping(source = "picture", target = "picture", qualifiedByName = "objectToString")
    @Mapping(source = "azp", target = "azp", qualifiedByName = "objectToString")
    @Mapping(source = "name", target = "name", qualifiedByName = "objectToString")
    @Mapping(source = "family_name", target = "familyName", qualifiedByName = "objectToString")
    @Mapping(source = "exp", target = "exp", qualifiedByName = "objectToLocalDateTime")
    @Mapping(source = "iat", target = "iat", qualifiedByName = "objectToLocalDateTime")
    @Mapping(source = "email", target = "email", qualifiedByName = "objectToString")
    @Mapping(source = "user", target = "user", qualifiedByName = "objectToUser")
    UserGoogleInfo userAttributesToUserGoogleInfo(Map<String, Object> attributes);

    @Named("objectToLong")
    default Long objectToLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    @Named("objectToString")
    default String objectToString(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    @Named("objectToBoolean")
    default Boolean objectToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    @Named("objectToUser")
    default User objectToUser(Object value) {
        if (value instanceof User) {
            return (User) value;
        }
        return null;
    }

    @Named("objectToLocalDateTime")
    default LocalDateTime objectToLocalDateTime(Object value) {
        String time = String.valueOf(value);

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(time);
        return zonedDateTime.toLocalDateTime();
    }

    @Mapping(source = "user.id", target = "userId")
    UserGoogleInfoDto toDto(UserGoogleInfo userGoogleInfo);

    UserGoogleInfo toEntity(UserGoogleInfoDto userGoogleInfoDto);
}
