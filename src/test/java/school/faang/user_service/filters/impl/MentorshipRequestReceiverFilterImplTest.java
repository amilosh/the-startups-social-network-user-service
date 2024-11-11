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
class MentorshipRequestReceiverFilterImplTest {
    private static final Long RECEIVER_ID = 1L;

    private final MentorshipRequestReceiverFilterImpl receiverFilter = new MentorshipRequestReceiverFilterImpl();

    @Test
    public void testIsApplicable() {
        RequestFilterDto filterDto = RequestFilterDto.builder().receiverId(RECEIVER_ID).build();

        boolean isApplicable = receiverFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        RequestFilterDto filterDto = new RequestFilterDto();

        boolean isApplicable = receiverFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyDescriptionFilter() {
        RequestFilterDto filterDto = RequestFilterDto.builder().receiverId(RECEIVER_ID).build();
        User firstUser = User.builder().id(RECEIVER_ID).build();
        User secondUser = User.builder().id(RECEIVER_ID + 1).build();
        MentorshipRequest firstRequest = MentorshipRequest.builder().receiver(firstUser).build();
        MentorshipRequest secondRequest = MentorshipRequest.builder().receiver(secondUser).build();

        List<MentorshipRequest> resultList = receiverFilter
                .apply(Stream.of(firstRequest, secondRequest), filterDto)
                .toList();

        assertEquals(1, resultList.size());
        assertEquals(RECEIVER_ID, resultList.get(0).getReceiver().getId());
    }
}