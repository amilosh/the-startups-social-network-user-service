package school.faang.user_service;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.ArrayList;
import java.util.List;

public class TestObjectGenerator {

    public User createUserTest() {
        return User.builder()
                .id(1L)
                .skills(List.of())
                .build();
    }

    public Skill createSkillTest() {
        return Skill.builder()
                .id(1L)
                .title("Java")
                .guarantees(new ArrayList<UserSkillGuarantee>())
                .build();
    }

    public Recommendation createRecommendationTest() {
        return Recommendation.builder()
                .id(1L)
                .author(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .skillOffers(new ArrayList<SkillOffer>())
                .build();
    }

    public UserSkillGuarantee createUserSkillGuaranteeTest() {
        return UserSkillGuarantee.builder()
                .id(1L)
                .user(createUserTest())
                .skill(createSkillTest())
                .guarantor(createUserTest())
                .build();
    }
}
