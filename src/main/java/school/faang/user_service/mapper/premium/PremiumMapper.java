package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PremiumMapper {
    @Mapping(source = "user.id", target = "userId")
    ResponsePremiumDto toDto(Premium premium);
}
