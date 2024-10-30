package school.faang.user_service.filter.mentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusFilterTest {
    StatusFilter requesterFilter = new StatusFilter();

    RequestFilterDto requestFilterDto;

    @BeforeEach
    public void setUp() {
        requestFilterDto = new RequestFilterDto();
    }

    @Test
    public void testIsApplied() {
        requestFilterDto.setStatus(null);
        boolean result = requesterFilter.isApplicable(requestFilterDto);
        assertFalse(result);
    }

    @Test
    public void testNotApplied() {
        requestFilterDto.setStatus("status");
        boolean result = requesterFilter.isApplicable(requestFilterDto);
        assertTrue(result);
    }

    @Test
    public void testStreamChanges() {
        requestFilterDto.setStatus("ACCEPTED");

        MentorshipRequest firstRequest = mock(MentorshipRequest.class);
        MentorshipRequest secondRequest = mock(MentorshipRequest.class);

        when(firstRequest.getStatus()).thenReturn(RequestStatus.PENDING);
        when(secondRequest.getStatus()).thenReturn(RequestStatus.ACCEPTED);

        Stream<MentorshipRequest> stream = Stream.of(firstRequest, secondRequest);
        List<MentorshipRequest> result = stream
                .filter(mentorshipRequest -> mentorshipRequest.getStatus().name().equals(requestFilterDto.getStatus()))
                .toList();

        assertEquals(1, result.size());
        assertEquals(secondRequest.getStatus(), result.get(0).getStatus());
    }
}
