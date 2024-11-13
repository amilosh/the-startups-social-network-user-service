package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RecommendationRequestMapperTest {

    private RecommendationRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RecommendationRequestMapper.class);
    }

    @Test
    void testToEntity_Success() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(1L);
        dto.setMessage("Test message");
        dto.setStatus(RequestStatus.PENDING);
        dto.setRequesterId(2L);
        dto.setReceiverId(3L);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setRejectionReason("No reason");
        dto.setSkills(Arrays.asList(10L, 20L));

        RecommendationRequest entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getMessage(), entity.getMessage());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals(dto.getRejectionReason(), entity.getRejectionReason());

        assertNull(entity.getSkills());
        assertNull(entity.getRequester());
        assertNull(entity.getReceiver());
        assertNull(entity.getRecommendation());
    }

    @Test
    void testToDto_Success() {
        RecommendationRequest entity = new RecommendationRequest();
        entity.setId(1L);
        entity.setMessage("Test message");
        entity.setStatus(RequestStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setRejectionReason("No reason");

        // Устанавливаем `requester` и `receiver`
        User requester = new User();
        requester.setId(2L);
        entity.setRequester(requester);

        User receiver = new User();
        receiver.setId(3L);
        entity.setReceiver(receiver);

        Skill skill1 = new Skill();
        skill1.setId(10L);

        Skill skill2 = new Skill();
        skill2.setId(20L);

        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setSkill(skill1);

        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest2.setSkill(skill2);

        entity.setSkills(Arrays.asList(skillRequest1, skillRequest2));

        RecommendationRequestDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getMessage(), dto.getMessage());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
        assertEquals(entity.getRejectionReason(), dto.getRejectionReason());
        assertEquals(entity.getRequester().getId(), dto.getRequesterId());
        assertEquals(entity.getReceiver().getId(), dto.getReceiverId());
        assertEquals(Arrays.asList(10L, 20L), dto.getSkills());
    }

    @Test
    void testMapSkills_NullOrEmpty() {
        List<Long> resultNull = mapper.mapSkills(null);
        assertNull(resultNull);

        List<Long> resultEmpty = mapper.mapSkills(Arrays.asList());
        assertNull(resultEmpty);
    }
}
