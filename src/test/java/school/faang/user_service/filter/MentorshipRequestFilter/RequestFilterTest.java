package school.faang.user_service.filter.MentorshipRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.TestDataCreator;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestFilterTest {
    private RequestFilterDto filterDto;
    private ReceiverIdFilter receiverIdFilter;
    private RequesterIdFilter requesterIdFilter;
    private DescriptionFilter descriptionFilter;
    private StatusFilter statusFilter;
    private Stream<MentorshipRequest> requestStream;

    @BeforeEach
    void setUp() {
        receiverIdFilter = new ReceiverIdFilter();
        requesterIdFilter = new RequesterIdFilter();
        descriptionFilter = new DescriptionFilter();
        statusFilter = new StatusFilter();

        filterDto = TestDataCreator.createRequestFilterDto(1L, 2L, "HELP", RequestStatus.ACCEPTED);
        User user1 = TestDataCreator.createUser(1L);
        User user2 = TestDataCreator.createUser(2L);
        MentorshipRequest request1 = TestDataCreator.createMentorshipRequest(1L, user1, user2, RequestStatus.ACCEPTED,
                "Help me with java!");
        MentorshipRequest request2 = TestDataCreator.createMentorshipRequest(2L, user2, user1, RequestStatus.PENDING,
                "Need assistance with java.");
        requestStream = Stream.of(request1, request2);
    }

    @Test
    void testRequesterIdFilterIsApplicable() {
        assertTrue(requesterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testRequesterIdFilterIsNotApplicable() {
        filterDto.setRequesterId(null);
        assertFalse(requesterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testReceiverIdFilterIsApplicable() {
        assertTrue(receiverIdFilter.isApplicable(filterDto));
    }

    @Test
    void testReceiverIdFilterIsNotApplicable() {
        filterDto.setReceiverId(null);
        assertFalse(receiverIdFilter.isApplicable(filterDto));
    }

    @Test
    void testDescriptionFilterIsApplicable() {
        assertTrue(descriptionFilter.isApplicable(filterDto));
    }

    @Test
    void testDescriptionFilterIsNotApplicable() {
        filterDto.setDescriptionPattern(null);
        assertFalse(descriptionFilter.isApplicable(filterDto));
    }

    @Test
    void testStatusFilterIsApplicable() {
        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testStatusFilterIsNotApplicable() {
        filterDto.setStatus(null);
        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testRequesterIdFilter() {
        requesterIdFilter.apply(requestStream, filterDto).forEach(request ->
                assertEquals(request.getRequester().getId(), filterDto.getRequesterId()));
    }

    @Test
    void testReceiverIdFilter() {
        receiverIdFilter.apply(requestStream, filterDto).forEach(request ->
                assertEquals(request.getReceiver().getId(), filterDto.getReceiverId()));
    }

    @Test
    void testDescriptionFilter() {
        descriptionFilter.apply(requestStream, filterDto).forEach(request ->
                assertTrue(request.getDescription().toLowerCase()
                        .contains(filterDto.getDescriptionPattern().toLowerCase())));
    }

    @Test
    void testStatusFilter() {
        statusFilter.apply(requestStream, filterDto).forEach(request ->
                assertEquals(request.getStatus(), filterDto.getStatus()));
    }
}