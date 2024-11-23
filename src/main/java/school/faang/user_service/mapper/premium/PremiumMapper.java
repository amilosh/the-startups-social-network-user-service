package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PremiumMapper {

    @Mapping(source = "userId", target = "user.id")
    Premium toEntity(PremiumDto premiumDto);

    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium premium);
}
