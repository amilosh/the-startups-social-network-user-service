package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
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

        receiver.getReceivedMentorshipRequests().add(mentorshipRequest);
        requester.getSentMentorshipRequests().add(mentorshipRequest);

        mentorshipRequestRepository.save(mentorshipRequest);
        userRepository.save(requester);
        userRepository.save(receiver);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilter) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();
        requestFilters.stream().filter(filter -> filter.isApplicable(requestFilter)).forEach(filter -> filter.apply(mentorshipRequests, requestFilter));

        return mentorshipRequestMapper.toMentorshipRequestDtoList(mentorshipRequests.toList());
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = validator.validateRequestId(id);
        long receiverId = mentorshipRequest.getReceiver().getId();
        long requesterId = mentorshipRequest.getRequester().getId();
        User receiver = validator.validateId(receiverId);
        User requester = validator.validateId(requesterId);

        if (receiver.getMentees().contains(requester)) {
            throw new IllegalArgumentException("Id " + requesterId + " is already on the mentis list " + receiverId);
        }
        receiver.getMentees().add(requester);
        requester.getMentors().add(receiver);
        userRepository.save(requester);
        userRepository.save(receiver);

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = validator.validateRequestId(id);
        String reason = rejection.getReason();
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(reason);
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}
