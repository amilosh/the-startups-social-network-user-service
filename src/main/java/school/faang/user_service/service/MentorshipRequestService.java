package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestMapper;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    private final MentorshipRequestMapper mentorshipRequestMapper;

    @Autowired
    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository, UserRepository userRepository, MentorshipRequestMapper mentorshipRequestMapper) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.userRepository = userRepository;
        this.mentorshipRequestMapper = mentorshipRequestMapper;
    }

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long idRequester = mentorshipRequestDto.getRequesterId();
        long idReceiver = mentorshipRequestDto.getReceiverId();
        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(idRequester, idReceiver);

        if (userRepository.existsById(idRequester)
                && userRepository.existsById(idReceiver)
                && (idRequester != idReceiver)) {
            if (lastRequest.isPresent() && lastRequest.get().getCreatedAt()
                    .isBefore(LocalDateTime.now().minusMonths(3))) {
                mentorshipRequestRepository.save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
            }

        }
    }
}