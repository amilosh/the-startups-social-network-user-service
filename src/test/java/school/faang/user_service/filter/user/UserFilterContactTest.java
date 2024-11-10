package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.datatest.DataSubscription;
import school.faang.user_service.dto.UserFilterDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserFilterContactTest {

    @InjectMocks
    private UserFilterContact filter;

    @Test
    void applySuccessWithInitValue() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoWrongValues(null, null);
        userFilterDto.setContactPattern(DataSubscription.getUserFilterDtoInitValues(null, null).getContactPattern());

        assertTrue(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF001 - should be true");
    }

    @Test
    void applySuccessWithNullValue() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoWrongValues(null, null);
        userFilterDto.setContactPattern(null);

        assertTrue(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF002 - should be true");
    }


    @Test
    void applyNegativeWrongValue() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoInitValues(null, null);
        userFilterDto.setContactPattern(DataSubscription.getUserFilterDtoWrongValues(null, null).getContactPattern());
        assertFalse(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF003 - should be false");
    }
}