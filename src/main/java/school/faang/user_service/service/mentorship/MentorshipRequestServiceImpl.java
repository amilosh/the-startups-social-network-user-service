package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.mentorship.MentorshipRequestDtoValidator;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestDtoValidator requestValidator;
    private final MentorshipRequestMapper requestMapper;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto creationRequestDto) {
        Long requesterId = creationRequestDto.getRequesterId();
        Long receiverId = creationRequestDto.getReceiverId();

        log.info(
                "Received a mentorship request! Sender ID - {}, Receiver ID - {}.",
                requesterId,
                receiverId
        );
        requestValidator.validateCreationRequest(creationRequestDto);

        MentorshipRequest request = requestMapper.toEntity(creationRequestDto);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);
        log.info(
                "The mentorship request has been saved in data base! Requester ID - {}, receiver ID - {}, date of creation - {}",
                requesterId, receiverId, creationRequestDto.getCreatedAt()
        );

        return requestMapper.toDto(savedRequest);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {

    }
}