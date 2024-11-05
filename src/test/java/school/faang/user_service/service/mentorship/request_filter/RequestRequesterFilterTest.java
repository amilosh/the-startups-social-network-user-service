package school.faang.user_service.service.mentorship.request_filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestRequesterFilterTest {
    private RequestFilterDto requestDto;
    private RequestRequesterFilter requestRequesterFilter;
    private Stream<MentorshipRequest> mentorshipRequestStream;

    @BeforeEach
    public void initData() {
        User firstUser = User.builder()
                .id(1L)
                .build();
        User secondUser = User.builder()
                .id(2L)
                .build();
        requestDto = new RequestFilterDto();
        requestDto.setRequesterId(1L);
        requestRequesterFilter = new RequestRequesterFilter();
        mentorshipRequestStream = Stream.of(
                MentorshipRequest.builder().requester(firstUser).build(),
                MentorshipRequest.builder().requester(secondUser).build());
    }

    @Test
    public void testApply() {
        List<MentorshipRequest> mentorshipRequests = requestRequesterFilter
                .apply(mentorshipRequestStream, requestDto)
                .stream()
                .toList();
        assertEquals(1, mentorshipRequests.size());
        mentorshipRequests.forEach(mentorshipRequest ->
                assertEquals(mentorshipRequest.getRequester().getId(), requestDto.getRequesterId()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(requestRequesterFilter.isApplicable(requestDto));
        assertFalse(requestRequesterFilter.isApplicable(new RequestFilterDto()));
    }
}
