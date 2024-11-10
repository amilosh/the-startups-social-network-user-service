package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long idRequester = mentorshipRequestDto.requesterId();
        long idReceiver = mentorshipRequestDto.receiverId();
        validateRequesterReceiver(idRequester, idReceiver);
        validateRequestDate(idRequester, idReceiver);
        mentorshipRequestRepository.create(idRequester, idReceiver, mentorshipRequestDto.description());
    }


    public void validateRequesterReceiver(long idRequester, long idReceiver) {
        if (!userRepository.existsById(idRequester)) {
            log.error("Requester not found: idReq - " + idRequester);
            throw new IllegalArgumentException(String.format("Requester id=%s not found", idRequester));
        }
        if (!userRepository.existsById(idReceiver)) {
            log.error("Receiver not found: idRec - " + idReceiver);
            throw new IllegalArgumentException(String.format("Receiver id=%s not found", idReceiver));
        }
        if (idRequester == idReceiver) {
            log.error("Requester and Receiver are the same person: idReq - " + idRequester + "idRec - " + idReceiver);
            throw new IllegalArgumentException(String.format("Requester id=%s or Receiver id=%s are the same person", idRequester, idReceiver));
        }
    }


    public void validateRequestDate(long idRequester, long idReceiver) {
        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(idRequester, idReceiver);
        if (lastRequest.isPresent()) {
            if (!lastRequest.get().getCreatedAt()
                    .isBefore(LocalDateTime.now().minusMonths(3))) {
                log.error("Last request has date less 3 months: " + lastRequest.get().getCreatedAt());
                throw new IllegalArgumentException(String.format("Last request has date less 3 months, %s", lastRequest.get().getCreatedAt()));
            }
        }
    }

    public List<MentorshipRequestDto> getRequest(String description, Long requesterId, Long receiverId, RequestStatusDto status) {
        List<MentorshipRequest> allMentorshipRequest = mentorshipRequestRepository.findAll();
        return allMentorshipRequest.stream()
                .filter(request ->
                        (description == null ||
                                request.getDescription().contains(description)) &&
                                (requesterId == null ||
                                        request.getRequester().getId().equals(requesterId)) &&
                                (receiverId == null ||
                                        request.getReceiver().getId().equals(receiverId)) &&
                                (status == null ||
                                        request.getStatus().toString().equals(status.toString())))
                .map(mentorshipRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptRequest(long id) {
        User requester = getRequesterByIdRequest(id);
        User receiver = getReceiverByIdRequest(id);

        if (requester.getMentors().contains(receiver)) {
            log.error(receiver
                    + "is a mentor for this requester");
            throw new IllegalArgumentException(receiver
                    + "is a mentor for this requester");
        } else {
            requester.getMentors().add(receiver);
            mentorshipRequestRepository.findById(id).get().setStatus(RequestStatus.ACCEPTED);
        }
    }

    @Transactional
    public void rejectRequest(long id, String rejection) {
        mentorshipRequestRepository.findById(id)
                .ifPresentOrElse(
                        mentorshipRequest -> {
                            mentorshipRequest.setStatus(RequestStatus.REJECTED);
                            mentorshipRequest.setRejectionReason(rejection);
                        },
                        () ->
                        {
                            log.error(String.format("Request with id: %s does not exist", id));
                            throw new IllegalArgumentException(String.format("Request with id: %s does not exist", id));
                        }
                );
    }

    private User getRequesterByIdRequest(long id) {
        return mentorshipRequestRepository.findById(id)
                .map(MentorshipRequest::getRequester)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Request with id: %s does not exist", id)));
    }

    private User getReceiverByIdRequest(long id) {
        return mentorshipRequestRepository.findById(id)
                .map(MentorshipRequest::getReceiver)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Request with id: %s does not exist", id)));
    }
}