package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {
    MentorshipRequest toEntity(MentorshipRequestDto requestDto);

    MentorshipRequestDto toDto(MentorshipRequest request);

    List<MentorshipRequestDto> toDtoList(List<MentorshipRequest> requests);
}
