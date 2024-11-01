package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapstruct.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final int THREE_MONTHS = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getRequesterId())) {
            throw new IllegalArgumentException("You cannot request mentorship from yourself");
        }
       User requester = userRepository.findById(mentorshipRequestDto.getRequesterId())
               .orElseThrow(()-> new EntityNotFoundException("User not found by Id: " + mentorshipRequestDto.getRequesterId()));
        if (!requester.getSentMentorshipRequests().isEmpty()) {
            List<MentorshipRequest> sortedRequests = requester.getSentMentorshipRequests().stream()
                    .sorted((mentorshipRequest1, mentorshipRequest2) ->
                            mentorshipRequest2.getCreatedAt().compareTo(mentorshipRequest1.getCreatedAt()))
                    .toList();
            LocalDateTime maxDateLastRequest = LocalDateTime.now().plusMonths(THREE_MONTHS);
            if (sortedRequests.get(0).getCreatedAt().isBefore(maxDateLastRequest)){
                //TODO: Create custom error
                throw new IllegalArgumentException("You can't request mentorship for less than 3 months");
            }
        }
       User receiver = userRepository.findById(mentorshipRequestDto.getReceiverId())
               .orElseThrow(()-> new EntityNotFoundException("User not found by Id: " + mentorshipRequestDto.getReceiverId()));

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.mapToEntity(mentorshipRequestDto);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        MentorshipRequest savedMentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.mapToDto(savedMentorshipRequest);
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto requestFilterDto) {
        List<MentorshipRequest> allRequests = mentorshipRequestRepository.findAll();

        List<MentorshipRequest> filteredRequests = new ArrayList<>();

        if (requestFilterDto.getStatus() != null) {
            filteredRequests.addAll(allRequests.stream()
                    .filter(res -> res.getStatus().equals(requestFilterDto.getStatus()))
                    .toList());
        }

        if (requestFilterDto.getRequesterId() != null) {
            filteredRequests.addAll(allRequests.stream()
                    .filter(res -> res.getRequester().getId().equals(requestFilterDto.getRequesterId()))
                    .toList());
        }

        if (requestFilterDto.getDescription() != null) {
            filteredRequests.addAll(allRequests.stream()
                    .filter(res -> res.getDescription().equals(requestFilterDto.getDescription()))
                    .toList());
        }
        return filteredRequests.stream().distinct().toList();
    }

    public Long acceptMentorship(Long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship request not found"));
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequest.getId();
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MentorshipRequest not found"));

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.mapToDto(mentorshipRequest);
    }
}
