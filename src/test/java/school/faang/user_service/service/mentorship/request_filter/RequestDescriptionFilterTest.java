package school.faang.user_service.service.mentorship.request_filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestDescriptionFilterTest {

    private RequestFilterDto requestDto;
    private RequestDescriptionFilter requestDescriptionFilter;
    private Stream<MentorshipRequest> mentorshipRequestStream;

    @BeforeEach
    public void initData() {
        requestDto = new RequestFilterDto();
        requestDto.setDescriptionPattern("описание");
        requestDescriptionFilter = new RequestDescriptionFilter();
        mentorshipRequestStream = Stream.of(
                MentorshipRequest.builder().description("описание 1").build(),
                MentorshipRequest.builder().description("описание 2").build(),
                MentorshipRequest.builder().description("ЗА КОРОЛЯ!!!").build()
        );
    }

    @Test
    public void testApply() {
        List<MentorshipRequest> mentorshipRequests = requestDescriptionFilter
                .apply(mentorshipRequestStream, requestDto)
                .stream()
                .toList();
        assertEquals(2, mentorshipRequests.size());
        mentorshipRequests.forEach(mentorshipRequest ->
                assertTrue(mentorshipRequest.getDescription().contains(requestDto.getDescriptionPattern())));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(requestDescriptionFilter.isApplicable(requestDto));
        assertFalse(requestDescriptionFilter.isApplicable(new RequestFilterDto()));
    }
}
