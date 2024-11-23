package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorsDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorsMapper {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "id", target = "id")
    List<MentorsDto> toDto(List<User> mentors);

    @InheritInverseConfiguration
    List<User> toEntity(List<MentorsDto> mentorsDtos);
}
