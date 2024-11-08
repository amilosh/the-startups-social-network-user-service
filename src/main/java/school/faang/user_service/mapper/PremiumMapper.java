package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PremiumMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "startDate", target = "startDate", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    @Mapping(source = "endDate", target = "endDate", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    PremiumDto toDto(Premium premium);

    @Mapping(target = "user", ignore = true)
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "stringToLocalDateTime")
    Premium toEntity(PremiumDto premiumDto);

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateTimeStr) {
        return DateFormatter.stringToLocalDateTime(dateTimeStr);
    }
}
