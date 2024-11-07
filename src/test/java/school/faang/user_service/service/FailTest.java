package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

public class FailTest {

    @Test
    public void faIlTest(){
        fail("Test Fail");
    }
}
