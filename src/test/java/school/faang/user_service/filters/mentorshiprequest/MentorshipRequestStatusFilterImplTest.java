package school.faang.user_service.filters.mentorshiprequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestStatusFilterImplTest {
    private static final RequestStatus REQUEST_STATUS = RequestStatus.ACCEPTED;

    private final MentorshipRequestStatusFilterImpl statusFilter = new MentorshipRequestStatusFilterImpl();

    @Test
    public void testIsApplicable() {
        RequestFilterDto filterDto = RequestFilterDto.builder().status(REQUEST_STATUS).build();

        boolean isApplicable = statusFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        RequestFilterDto filterDto = new RequestFilterDto();

        boolean isApplicable = statusFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyDescriptionFilter() {
        RequestFilterDto filterDto = RequestFilterDto.builder().status(REQUEST_STATUS).build();
        MentorshipRequest firstRequest = MentorshipRequest.builder().status(RequestStatus.PENDING).build();
        MentorshipRequest secondRequest = MentorshipRequest.builder().status(REQUEST_STATUS).build();

        List<MentorshipRequest> resultList = statusFilter
                .apply(Stream.of(firstRequest, secondRequest), filterDto)
                .toList();

        assertEquals(1, resultList.size());
        assertEquals(REQUEST_STATUS, resultList.get(0).getStatus());
    }
}