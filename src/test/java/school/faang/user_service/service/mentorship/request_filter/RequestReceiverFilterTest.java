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

public class RequestReceiverFilterTest {

    private RequestFilterDto requestDto;
    private RequestReceiverFilter requestReceiverFilter;
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
        requestDto.setReceiverId(1L);
        requestReceiverFilter = new RequestReceiverFilter();
        mentorshipRequestStream = Stream.of(
                MentorshipRequest.builder().receiver(firstUser).build(),
                MentorshipRequest.builder().receiver(secondUser).build());
    }

    @Test
    public void testApply() {
        List<MentorshipRequest> mentorshipRequests = requestReceiverFilter
                .apply(mentorshipRequestStream, requestDto)
                .stream()
                .toList();
        assertEquals(1, mentorshipRequests.size());
        mentorshipRequests.forEach(mentorshipRequest ->
                assertEquals(mentorshipRequest.getReceiver().getId(), requestDto.getReceiverId()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(requestReceiverFilter.isApplicable(requestDto));
        assertFalse(requestReceiverFilter.isApplicable(new RequestFilterDto()));
    }
}
