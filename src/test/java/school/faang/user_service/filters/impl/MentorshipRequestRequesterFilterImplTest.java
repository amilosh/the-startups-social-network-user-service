package school.faang.user_service.filters.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestRequesterFilterImplTest {
    private static final Long REQUESTER_ID = 1L;

    private final MentorshipRequestRequesterFilterImpl requesterFilter = new MentorshipRequestRequesterFilterImpl();

    @Test
    public void testIsApplicable() {
        RequestFilterDto filterDto = RequestFilterDto.builder().requesterId(REQUESTER_ID).build();

        boolean isApplicable = requesterFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        RequestFilterDto filterDto = new RequestFilterDto();

        boolean isApplicable = requesterFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyDescriptionFilter() {
        RequestFilterDto filterDto = RequestFilterDto.builder().requesterId(REQUESTER_ID).build();
        User firstUser = User.builder().id(REQUESTER_ID).build();
        User secondUser = User.builder().id(REQUESTER_ID + 1).build();
        MentorshipRequest firstRequest = MentorshipRequest.builder().requester(firstUser).build();
        MentorshipRequest secondRequest = MentorshipRequest.builder().requester(secondUser).build();

        List<MentorshipRequest> resultList = requesterFilter
                .apply(Stream.of(firstRequest, secondRequest), filterDto)
                .toList();

        assertEquals(1, resultList.size());
        assertEquals(REQUESTER_ID, resultList.get(0).getRequester().getId());
    }
}