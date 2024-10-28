package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Autowired
    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
    }

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(mentorshipRequestDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = mentorshipRequest.getCreatedAt();

        if (ChronoUnit.DAYS.between(createdAt, now) < 3) {
            throw new IllegalArgumentException("You can't request mentorship for less than 3 days");
        }
        mentorshipRequest.getReceiver().getMentors().add(mentorshipRequest.getRequester());
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto requestFilterDto) {
        List<MentorshipRequest> allRequests = mentorshipRequestRepository.findAll();

        if (requestFilterDto.getStatus() != null) {
            allRequests.stream()
                    .filter(res -> res.getDescription().equals(requestFilterDto.getDescription()))
                    .filter(res -> res.getRequester().getId().equals(requestFilterDto.getRequesterId()))
                    .filter(res -> res.getStatus().equals(requestFilterDto.getStatus()))
                    .toList();
        }
        return allRequests;
    }

    public Long acceptMentorship(Long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship request not found"));
        return mentorshipRequest.getId();
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship request not found"));
        rejection.setStatus(RequestStatus.REJECTED);
        rejection.setReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}
