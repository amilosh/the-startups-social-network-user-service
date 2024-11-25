package school.faang.user_service.filters.mentorshiprequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestDescriptionFilterImplTest {
    private static final String REQUEST_DESCRIPTION = "est1";

    private final MentorshipRequestDescriptionFilterImpl descriptionFilter = new MentorshipRequestDescriptionFilterImpl();

    @Test
    public void testIsApplicable() {
        RequestFilterDto filterDto = RequestFilterDto.builder().description(REQUEST_DESCRIPTION).build();

        boolean isApplicable = descriptionFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        RequestFilterDto filterDto = new RequestFilterDto();

        boolean isApplicable = descriptionFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyDescriptionFilter() {
        RequestFilterDto filterDto = RequestFilterDto.builder().description(REQUEST_DESCRIPTION).build();
        MentorshipRequest firstRequest = MentorshipRequest.builder().description("test1").build();
        MentorshipRequest secondRequest = MentorshipRequest.builder().description("test2").build();

        List<MentorshipRequest> resultList = descriptionFilter
                .apply(Stream.of(firstRequest, secondRequest), filterDto)
                .toList();

        assertEquals(1, resultList.size());
        assertTrue(resultList.get(0).getDescription().contains(REQUEST_DESCRIPTION));
    }
}