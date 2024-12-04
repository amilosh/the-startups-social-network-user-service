package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.Integrations.avatar.AvatarService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @InjectMocks
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        setField(avatarService, "baseUrl", "https://avatars.dicebear.com");
        setField(avatarService, "version", "v2");
    }

    @Test
    void testGetRandomAvatar_EmptyStyles() {
        setField(avatarService, "styles", new ArrayList<>());  // Пустой список стилей
        setField(avatarService, "seedNames", List.of("user1"));  // Список с seedNames

        RuntimeException exception = assertThrows(RuntimeException.class, avatarService::getRandomAvatar);
        assertTrue(exception.getMessage().contains("Failed to generate random avatar URL"));
        assertNotNull(exception.getCause());
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("Styles list is empty or not configured properly.", exception.getCause().getMessage());
    }

    @Test
    void testGetRandomAvatar_EmptySeedNames() {
        setField(avatarService, "styles", List.of("bottts"));
        setField(avatarService, "seedNames", new ArrayList<>());  // Пустой список seedNames

        RuntimeException exception = assertThrows(RuntimeException.class, avatarService::getRandomAvatar);
        assertTrue(exception.getMessage().contains("Failed to generate random avatar URL"));
        assertNotNull(exception.getCause());
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("Seed names list is empty or not configured properly.", exception.getCause().getMessage());
    }

    @Test
    void testGetRandomAvatar_EmptyBaseUrl() {
        setField(avatarService, "baseUrl", "");  // Пустой baseUrl
        setField(avatarService, "styles", List.of("bottts"));
        setField(avatarService, "seedNames", List.of("user1"));

        RuntimeException exception = assertThrows(RuntimeException.class, avatarService::getRandomAvatar);
        assertTrue(exception.getMessage().contains("Failed to generate random avatar URL"));
        assertNotNull(exception.getCause());
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("Base URL is not configured properly.", exception.getCause().getMessage());
    }

    @Test
    void testGetRandomAvatar_EmptyVersion() {
        setField(avatarService, "version", "");  // Пустая версия
        setField(avatarService, "styles", List.of("bottts"));
        setField(avatarService, "seedNames", List.of("user1"));

        RuntimeException exception = assertThrows(RuntimeException.class, avatarService::getRandomAvatar);
        assertTrue(exception.getMessage().contains("Failed to generate random avatar URL"));
        assertNotNull(exception.getCause());
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("API version is not configured properly.", exception.getCause().getMessage());
    }

    @Test
    void testGetRandomAvatar_Success() {
        setField(avatarService, "styles", List.of("bottts", "avataaars"));
        setField(avatarService, "seedNames", List.of("user1", "user2"));

        String avatarUrl = avatarService.getRandomAvatar();

        assertNotNull(avatarUrl);
        assertTrue(avatarUrl.contains("https://avatars.dicebear.com"));
        assertTrue(avatarUrl.contains("/v2/"));
        assertTrue(avatarUrl.contains("/svg?seed="));
    }

    @Test
    void testGetRandomAvatar_RuntimeException() {
        setField(avatarService, "styles", null);
        setField(avatarService, "seedNames", List.of("user1"));

        RuntimeException exception = assertThrows(RuntimeException.class, avatarService::getRandomAvatar);
        assertTrue(exception.getMessage().contains("Failed to generate random avatar URL"));
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }
}
