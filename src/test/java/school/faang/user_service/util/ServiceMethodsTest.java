package school.faang.user_service.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceMethodsTest {
    @Test
    void getTimeIsoOffsetDateTime() {
        assertNotNull(ServiceMethods.getTimeIsoOffsetDateTime(), "ESM001 - Expected a String.");
    }
}