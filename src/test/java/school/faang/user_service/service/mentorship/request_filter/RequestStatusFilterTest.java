package school.faang.user_service.service.mentorship.request_filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RequestStatusFilterTest {
    private RequestFilterDto requestDto;
    private RequestStatusFilter requestStatusFilter;
    private Stream<MentorshipRequest> mentorshipRequestStream;

    @BeforeEach
    public void initData() {
        requestDto = new RequestFilterDto();
        requestDto.setStatus(RequestStatus.ACCEPTED);
        requestStatusFilter = new RequestStatusFilter();
        mentorshipRequestStream = Stream.of(
                MentorshipRequest.builder().status(RequestStatus.ACCEPTED).build(),
                MentorshipRequest.builder().status(RequestStatus.PENDING).build());
    }

    @Test
    public void testApply() {
        List<MentorshipRequest> mentorshipRequests = requestStatusFilter
                .apply(mentorshipRequestStream, requestDto)
                .stream()
                .toList();
        assertEquals(1, mentorshipRequests.size());
        mentorshipRequests.forEach(mentorshipRequest ->
                assertSame(mentorshipRequest.getStatus(), requestDto.getStatus()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(requestStatusFilter.isApplicable(requestDto));
        assertFalse(requestStatusFilter.isApplicable(new RequestFilterDto()));
    }
}
