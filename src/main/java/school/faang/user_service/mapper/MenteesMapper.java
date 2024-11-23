package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MenteesDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenteesMapper {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "id", target = "id")
    List<MenteesDto> toDto(List<User> mentees);

    @InheritInverseConfiguration
    List<User> toEntity(List<MenteesDto> menteesDtos);
}
