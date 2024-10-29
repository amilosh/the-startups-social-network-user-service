package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    public MentorshipRequestDto createRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        // спросить по поводу обработки исключения, как мне например ловиь исключение валидаци и залогтровать его
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
