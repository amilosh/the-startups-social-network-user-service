package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.SkillOfferRepository;
import school.faang.user_service.service.skill_offer.SkillOfferService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillOfferServiceTest {
    @Mock
    SkillOfferRepository skillOfferRepository;

    @InjectMocks
    SkillOfferService skillOfferService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        when(skillOfferRepository.create(123L, 456L)).thenReturn(789L);

        Long result = skillOfferService.create(123L, 456L);

        assertEquals(789L, result);

        verify(skillOfferRepository).create(123L, 456L);
    }

    @Test
    void testDeleteAllByRecommendationId() {
        skillOfferService.deleteAllByRecommendationId(123L);

        verify(skillOfferRepository).deleteAllByRecommendationId(123L);
    }

    @Test
    void testCreateThrowsException() {
        when(skillOfferRepository.create(123L, 456L)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            skillOfferService.create(123L, 456L);
        });

        assertEquals("Database error", exception.getMessage());

        verify(skillOfferRepository).create(123L, 456L);
    }
}
