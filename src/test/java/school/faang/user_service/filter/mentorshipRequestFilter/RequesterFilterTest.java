package school.faang.user_service.filter.mentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequesterFilterTest {

    RequesterFilter requesterFilter = new RequesterFilter();

    RequestFilterDto requestFilterDto;

    @BeforeEach
    public void setUp() {
        requestFilterDto = new RequestFilterDto();
    }

    @Test
    public void testIsApplied() {
        requestFilterDto.setRequesterUserId(null);
        boolean result = requesterFilter.isApplicable(requestFilterDto);
        assertFalse(result);
    }

    @Test
    public void testNotApplied() {
        requestFilterDto.setRequesterUserId(1L);
        boolean result = requesterFilter.isApplicable(requestFilterDto);
        assertTrue(result);
    }

    @Test
    public void testStreamChanges() {
        requestFilterDto.setRequesterUserId(2L);

        MentorshipRequest firstRequest = mock(MentorshipRequest.class);
        MentorshipRequest secondRequest = mock(MentorshipRequest.class);

        User firstReceiverUser = mock(User.class);
        User secondReceiverUser = mock(User.class);

        when(firstRequest.getRequester()).thenReturn(firstReceiverUser);
        when(secondRequest.getRequester()).thenReturn(secondReceiverUser);

        when(firstReceiverUser.getId()).thenReturn(1L);
        when(secondReceiverUser.getId()).thenReturn(2L);

        Stream<MentorshipRequest> stream = Stream.of(firstRequest, secondRequest);
        List<MentorshipRequest> result = stream
                .filter(mentorshipRequest -> mentorshipRequest.getRequester().getId().equals(requestFilterDto.getRequesterUserId()))
                .toList();

        assertEquals(1, result.size());
        assertEquals(secondReceiverUser, result.get(0).getRequester());
    }
}
