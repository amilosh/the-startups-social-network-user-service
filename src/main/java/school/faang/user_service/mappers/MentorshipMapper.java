package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.MentorshipDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipMapper {
    MentorshipDto toDto(User user);
}
