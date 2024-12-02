package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.premium.Premium;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PremiumServiceValidatorTest {


    @Test
    void testNotNullListForCheckListForNull(){
        assertDoesNotThrow(()->PremiumServiceValidator.checkListForNull(List.of()));
    }
    @Test
    void testNullListForCheckListForNull(){
        assertThrows(IllegalArgumentException.class,
                ()->PremiumServiceValidator.checkListForNull(null));
    }
    @Test
    void testNotNullListForCheckPremiumNotNull(){
        Premium premium = new Premium();
        assertDoesNotThrow(()->PremiumServiceValidator.checkPremiumNotNull(premium));
    }
    @Test
    void testNullListForCheckPremiumNotNull(){
        assertThrows(IllegalArgumentException.class,
                ()->PremiumServiceValidator.checkPremiumNotNull(null));
    }
}