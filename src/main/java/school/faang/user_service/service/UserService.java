package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public UserSkillGuarantee addGuaranty(long userId, SkillOffer skillOffer) {
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder().user(
                        userRepository.findById(userId).get()
                ).guarantor(skillOffer.getRecommendation().getAuthor())
                .build();
        return userSkillGuaranteeRepository.save(guarantee);
    }

}
