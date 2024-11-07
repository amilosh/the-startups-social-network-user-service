package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SomeFailedTest {
    @Test
    public void testTrue() {
        boolean result = true;
        assertTrue(result);
    }

    @Test
    public void testFailed() {
        fail();
    }
}
