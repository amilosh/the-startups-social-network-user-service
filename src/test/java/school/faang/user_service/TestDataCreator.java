package school.faang.user_service;

import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

public class TestDataCreator {

    private TestDataCreator() {
    }

    public static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public static MentorshipRequest createMentorshipRequest(Long id, User requester, User receiver,
                                                            RequestStatus status, String description) {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(id);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(status);
        request.setDescription(description);
        return request;
    }

    public static MentorshipRequestDto createMentorshipRequestDto(Long id, Long requesterId, Long receiverId,
                                                                  RequestStatus status, String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setId(id);
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);
        dto.setStatus(status);
        dto.setDescription(description);
        return dto;
    }

    public static MentorshipRequestDto createMentorshipRequestDto(Long requesterId, Long receiverId,
                                                                  String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);
        dto.setDescription(description);
        return dto;
    }

    public static RequestFilterDto createRequestFilterDto(Long requesterId, Long receiverId, String descriptionPattern,
                                                          RequestStatus status) {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);
        dto.setDescriptionPattern(descriptionPattern);
        dto.setStatus(status);
        return dto;
    }
}
