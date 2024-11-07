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
import school.faang.user_service.mapper.mentorship_request.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.request_filter.RequestFilter;
import school.faang.user_service.validator.mentorship_request.MentorshipRequestValidation;

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
        validator.validate3MonthsFromTheLastRequest(requester,receiver);

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
        requestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilter))
                .forEach(filter -> filter.apply(mentorshipRequests, requestFilter));
        log.info("Getting a list of mentoring requests after filtering");
        return mentorshipRequestMapper.toMentorshipRequestDtoList(mentorshipRequests.toList());
    }

    public void acceptRequest(long mentorshipRequestId) {
        MentorshipRequest mentorshipRequest = validator.validateRequestId(mentorshipRequestId);
        validator.validateOfBeingInMentorship(mentorshipRequest);
        validator.validateStatus(mentorshipRequest);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();
        requester.getMentors().add(receiver);
        log.info("The receiver {} was successfully added to the requesters {} list of mentors"
                , receiver.getId()
                , requester.getId());
        saveChangesOfUserInDB(requester);
        log.info("The requester {} was successfully saved to DB", requester.getId());
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        saveChangesOfMentorshipRequestsInDB(mentorshipRequest);
        log.info("Mentorship request with status ACCEPTED and requestId {} saved", mentorshipRequest.getId());
    }

    public void rejectRequest(long mentorshipRequestId, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = validator.validateRequestId(mentorshipRequestId);
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
