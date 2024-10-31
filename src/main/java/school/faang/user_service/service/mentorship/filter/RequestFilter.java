package school.faang.user_service.service.mentorship.filter;

import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;

import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplicable(RequestFilterDto filterDto);

    Stream<MentorshipRequestDto> apply(Stream<MentorshipRequestDto> requestDtos, RequestFilterDto filterDto);
}
