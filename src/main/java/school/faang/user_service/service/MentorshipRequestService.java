package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {

        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();

        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .forEach(filter -> filter.apply(mentorshipRequestStream, requestFilterDto));

        return mentorshipRequestStream.map(mentorshipRequestMapper::toDto).toList();
    }

    public MentorshipRequestDto createRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        // спросить по поводу обработки исключения, как мне например ловить исключение валидации и залогтровать его
        mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto);

        mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterUserId(),
                mentorshipRequestDto.getReceiverUserId(),
                mentorshipRequestDto.getDescription()
        );

        return mentorshipRequestDto;
    }

    public Optional<MentorshipRequest> findLatestRequest(long requesterId, long receiverId) {
        return mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);
    }
}
