package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.MentorshipRequestedEventDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface MentorshipRequestedEventMapper {

    @Mapping(target = "requestedAt", expression = "java(java.time.LocalDateTime.now())")
    MentorshipRequestedEventDto toMentorshipRequestedEventDto(MentorshipRequest requestDto);
}