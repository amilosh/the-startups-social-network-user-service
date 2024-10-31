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
import school.faang.user_service.validator.ValidationContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final List<MentorshipRequestValidator> requestValidators;
    private final UserService userService;

    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {

        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();

        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .forEach(filter -> filter.apply(mentorshipRequestStream, requestFilterDto));

        // спросить про то зачем нам нужен маппер toEntity если мы всегда будет работать с данными который отправил пользователь, а они есть в dto
        return mentorshipRequestStream.map(mentorshipRequestMapper::toDto).toList();
    }

    public MentorshipRequestDto createRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        ValidationContext context = new ValidationContext(this, userService);

        // спросить по поводу обработки исключения, как мне например ловить исключение валидации и залогтровать его
        requestValidators.forEach(validator -> validator.validate(mentorshipRequestDto, context));

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
