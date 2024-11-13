package school.faang.user_service.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.mapper.MenteesMapperImpl;
import school.faang.user_service.mapper.MentorsMapperImpl;
import school.faang.user_service.repository.UserRepository;

public class MentorshipServiceTest {
    @Spy
    private MenteesMapperImpl menteesMapper;
    @Spy
    private MentorsMapperImpl mentorsMapper;
    @Mock
    private  UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;


}
