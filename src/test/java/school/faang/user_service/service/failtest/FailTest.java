package school.faang.user_service.service.failtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailTest {
    @Test
    void testFail() {
        fail("failTest");
    }
}
