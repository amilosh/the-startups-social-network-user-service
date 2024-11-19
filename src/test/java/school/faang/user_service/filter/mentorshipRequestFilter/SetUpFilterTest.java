package school.faang.user_service.filter.mentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public abstract class SetUpFilterTest {
    protected RequestFilterDto filterDto;
    protected Stream<MentorshipRequest> requestStream;

    @BeforeEach
    void setUp() {
        filterDto = RequestFilterDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .descriptionPattern("HELP")
                .status(RequestStatus.ACCEPTED)
                .build();

        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();

        MentorshipRequest firstRequest = MentorshipRequest.builder()
                .id(1L)
                .requester(firstUser)
                .receiver(secondUser)
                .status(RequestStatus.ACCEPTED)
                .description("Help me with java!")
                .build();

        MentorshipRequest secondRequest = MentorshipRequest.builder()
                .id(2L)
                .requester(secondUser)
                .receiver(firstUser)
                .status(RequestStatus.PENDING)
                .description("Need assistance with java.")
                .build();

        requestStream = Stream.of(firstRequest, secondRequest);
    }
}