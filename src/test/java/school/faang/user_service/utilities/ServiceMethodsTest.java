package school.faang.user_service.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceMethodsTest {
    @Test
    void getTimeIsoOffsetDateTimeSuccessTest() {
        assertNotNull(ServiceMethods.getTimeIsoOffsetDateTime(), "ESM001 - Expected a String.");
    }
}