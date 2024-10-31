package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.util.CollectionUtils;
import school.faang.user_service.util.SkillUtils;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;

import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final long REQUIRED_DURATION_IN_DAYS = 6 * 30;

    private final RecommendationRepository recommendationRepo;

    private final UserService userService;
    private final SkillService skillService;

    private final RecommendationMapper recommendationMapper;

    private final RecommendationValidator recommendationValidator;
    private final SkillValidator skillValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = mapToFullRecommendation(recommendationDto);
        validateNewRecommendation(recommendation);

        addSkillToReceiverIfAbsent(recommendation);
        addGuaranteeToReceiverSkillIfAbsent(recommendation);

        recommendation = recommendationRepo.save(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long recommendationId) {
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        List<Recommendation> otherRecommendation = CollectionUtils.excludeItemFrom(
                recommendationRepo.findAllByReceiverIdAndAuthorId(receiver.getId(), author.getId()),
                recommendation);
        List<Recommendation> receiverRecommendations = CollectionUtils.excludeItemFrom(
                recommendationRepo.findAllByReceiverId(receiver.getId()),
                recommendation);
        List<Skill> offeredSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());

        removeGuaranteeIfNoOtherRecommendations(otherRecommendation, offeredSkills, receiver, author);
        removeSkillIfNoOtherGuarantees(receiverRecommendations, receiver, offeredSkills);
        receiver.removeReceivedRecommendation(recommendation);
        author.removeGivenRecommendation(recommendation);
        recommendationRepo.delete(recommendation);
    }

    public RecommendationDto update(long recommendationId, RecommendationDto recommendationDto) {
        return recommendationDto;
    }

    private void removeSkillIfNoOtherGuarantees(List<Recommendation> receiverRecommendations,
                                                User receiver, List<Skill> skills) {
        Set<Skill> skillsOther = new HashSet<>(
                SkillUtils.getSkillsFromRecommendations(receiverRecommendations));
        skills.stream()
                .filter(skill -> !skillsOther.contains(skill))
                .forEach(skill -> {
                    receiver.removeSkill(skill);
                    skill.removeUser(receiver);
                });
    }

    private void removeGuaranteeIfNoOtherRecommendations(List<Recommendation> otherRecommendations,
                                                         List<Skill> skills, User receiver, User author) {
        Set<Skill> skillsOfOtherRecommends = new HashSet<>(
                SkillUtils.getSkillsFromRecommendations(otherRecommendations));
        skills.stream()
                .filter(skill -> !skillsOfOtherRecommends.contains(skill))
                .forEach(skill -> skill.removeSameGuarantee(new UserSkillGuarantee(receiver, skill, author)));
    }

    private static void addSkillToReceiverIfAbsent(Recommendation recommendation) {
        User receiver = recommendation.getReceiver();
        List<Skill> recommendationSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());
        Set<Skill> receiverSkills = new HashSet<>(receiver.getSkills());

        CollectionUtils.filterAndProcess(recommendationSkills,
                skill -> {
                    receiver.addSkill(skill);
                    skill.addUser(receiver);
                },
                skill -> !receiverSkills.contains(skill));
    }

    private void addGuaranteeToReceiverSkillIfAbsent(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();
        List<Skill> recommendationSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());

        CollectionUtils.filterAndProcess(receiver.getSkills(),
                skill -> {
                    UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                            .user(receiver)
                            .skill(skill)
                            .guarantor(author)
                            .build();
                    skill.addGuarantee(guarantee);
                },
                recommendationSkills::contains,
                skill -> !skill.userIsGuarantor(author)
        );
    }

    private void validateNewRecommendation(Recommendation newRecommendation) {
        if (newRecommendation == null) {
            throw new DataValidationException("Recommendation is null");
        }
        if (!recommendationValidator.isPeriodElapsedSinceLastRecommendation(
                getLastReceiverRecommendationFromAuthor(newRecommendation),
                newRecommendation,
                Duration.ofDays(REQUIRED_DURATION_IN_DAYS))) {
            throw new DataValidationException("Less than 6 months since last referral");
        }
        skillValidator.validateSkillsForExistence(SkillUtils.toSkillList(
                newRecommendation.getSkillOffers()
        ));
    }

    private Recommendation getLastReceiverRecommendationFromAuthor(Recommendation recommendation) {
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        return recommendationRepo
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(author.getId(), receiver.getId())
                .orElse(null);
    }

    @NotNull
    private Recommendation mapToFullRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new IllegalArgumentException("recommendationDto must not be null");
        }

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        User author = userService.getUserById(recommendationDto.authorId());
        User receiver = userService.getUserById(recommendationDto.receiverId());
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);

        Queue<Skill> skills = new LinkedList<>(skillService.getSkillsFrom(recommendationDto.skillOffers()));
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            skillOffer.setRecommendation(recommendation);
            skillOffer.setSkill(skills.poll());
        }
        return recommendation;
    }
}
