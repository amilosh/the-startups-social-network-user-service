package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring")
public interface PremiumMapper {

    Premium toEntity(PremiumDto premiumDto);

    PremiumDto toDto(Premium premium);
}
