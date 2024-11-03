package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.request_filter.RequestFilter;
import school.faang.user_service.validation.MentorshipRequestValidation;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<RequestFilter> requestFilters;
    private final MentorshipRequestValidation validator;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long receiverId = mentorshipRequestDto.getReceiverId();
        long requesterId = mentorshipRequestDto.getRequesterId();
        validator.validateSameId(receiverId, requesterId);

        User receiver = validator.validateId(receiverId);
        User requester = validator.validateId(requesterId);
        validator.validate3MonthsFromTheLastRequest(requester);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toMentorshipRequest(mentorshipRequestDto);

        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);

        requester.getSentMentorshipRequests().add(mentorshipRequest);
        log.info("Mentorship request is saved in the list of the sender with ID {}", requesterId);

        saveChangesOfMentorshipRequestsInDB(mentorshipRequest);
        log.info("Mentorship request with requestId {} saved", mentorshipRequest.getId());
        saveChangesOfUserInDB(requester);
        log.info("Requester with ID {} is saved ib DB", requesterId);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilter) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();
        requestFilters.stream().filter(filter -> filter.isApplicable(requestFilter)).forEach(filter -> filter.apply(mentorshipRequests, requestFilter));
        log.info("Getting a list of mentoring requests after filtering");
        return mentorshipRequestMapper.toMentorshipRequestDtoList(mentorshipRequests.toList());
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = validator.validateRequestId(id);
        long receiverId = mentorshipRequest.getReceiver().getId();
        long requesterId = mentorshipRequest.getRequester().getId();
        User receiver = validator.validateId(receiverId);
        User requester = validator.validateId(requesterId);

        validator.validateOfBeingInMentorship(receiver, requester);

        requester.getMentors().add(receiver);
        log.info("The receiver {} was successfully added to the requesters {} list of mentors", receiverId, requesterId);
        saveChangesOfUserInDB(requester);
        log.info("The requester {} was successfully saved to DB", requesterId);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        saveChangesOfMentorshipRequestsInDB(mentorshipRequest);
        log.info("Mentorship request with status ACCEPTED and requestId {} saved", mentorshipRequest.getId());
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = validator.validateRequestId(id);
        String reason = rejection.getReason();

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(reason);

        saveChangesOfMentorshipRequestsInDB(mentorshipRequest);
        log.info("Mentorship request with status REJECTED and requestId {} saved", mentorshipRequest.getId());

    }

    private void saveChangesOfUserInDB(User user) {
        userRepository.save(user);
    }

    private void saveChangesOfMentorshipRequestsInDB(MentorshipRequest mentorshipRequest) {
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}
