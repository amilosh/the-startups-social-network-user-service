package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.util.CollectionUtils;
import school.faang.user_service.util.SkillUtils;
import school.faang.user_service.validator.RecommendationValidator;

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
    private final SkillOfferRepository skillOfferRepo;

    private final UserService userService;
    private final SkillService skillService;

    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;

    private final RecommendationValidator recommendationValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = mapToFullRecommendation(recommendationDto);
        validateRecommendation(recommendation);

        List<Skill> recommendationSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());

        addSkillToReceiverIfAbsent(recommendation, recommendationSkills);
        addGuaranteeToReceiverSkillIfAbsent(recommendation, recommendationSkills);

        recommendation = recommendationRepo.save(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long recommendationId) {
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        List<Skill> offeredSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());

        removeGuaranteeIfNoOtherRecommendations(recommendation, offeredSkills);
        removeUserSkillIfNoOtherGuarantees(recommendation, offeredSkills);
        receiver.removeReceivedRecommendation(recommendation);
        author.removeGivenRecommendation(recommendation);
        recommendationRepo.delete(recommendation);
    }

    @Transactional
    public RecommendationDto update(long recommendationId, RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));
        validateRecommendation(recommendation);

        List<Skill> oldSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());
        List<Skill> updatedSkills = skillService.getSkillsFrom(recommendationDto.skillOffers());
        List<Skill> removedSkills = CollectionUtils.findMissingElements(oldSkills, updatedSkills);
        List<Skill> newSkills = CollectionUtils.findMissingElements(updatedSkills, oldSkills);

        recommendation.setContent(recommendationDto.content());
        recommendation.updateSkillOffers(recommendationDto.skillOffers().stream()
                .map(skillOfferDto -> mapToFullSkillOffer(skillOfferDto, recommendation))
                .toList()
        );

        removeGuaranteeIfNoOtherRecommendations(recommendation, removedSkills);
        removeUserSkillIfNoOtherGuarantees(recommendation, removedSkills);
        addSkillToReceiverIfAbsent(recommendation, newSkills);
        addGuaranteeToReceiverSkillIfAbsent(recommendation, newSkills);

        Recommendation updatedRecommendation = recommendationRepo.save(recommendation);
        return recommendationMapper.toDto(updatedRecommendation);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        List<Recommendation> recommendationsReceived = recommendationRepo.findAllByReceiverId(receiverId);
        return recommendationMapper.toDtoList(recommendationsReceived);
    }

    private void removeUserSkillIfNoOtherGuarantees(Recommendation recommendation, List<Skill> removedSkills) {
        User receiver = recommendation.getReceiver();
        List<Recommendation> otherReceiverRecommendations = CollectionUtils.excludeItemFrom(
                recommendationRepo.findAllByReceiverId(receiver.getId()),
                recommendation);
        Set<Skill> skillsOtherReceiverRecommendations = new HashSet<>(
                SkillUtils.getSkillsFromRecommendations(otherReceiverRecommendations));

        removedSkills.stream()
                .filter(skill -> !skillsOtherReceiverRecommendations.contains(skill))
                .forEach(skill -> {
                    receiver.removeSkill(skill);
                    skill.removeUser(receiver);
                });
    }

    private void removeGuaranteeIfNoOtherRecommendations(Recommendation recommendation, List<Skill> guaranteedSkills) {
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        List<Recommendation> otherRecommendations = CollectionUtils.excludeItemFrom(
                recommendationRepo.findAllByReceiverIdAndAuthorId(receiver.getId(), author.getId()),
                recommendation);
        Set<Skill> skillsOfOtherRecommends = new HashSet<>(
                SkillUtils.getSkillsFromRecommendations(otherRecommendations));

        guaranteedSkills.stream()
                .filter(skill -> !skillsOfOtherRecommends.contains(skill))
                .forEach(skill -> skill.removeSameGuarantee(new UserSkillGuarantee(receiver, skill, author)));
    }

    private static void addSkillToReceiverIfAbsent(Recommendation recommendation, List<Skill> addedSkills) {
        User receiver = recommendation.getReceiver();
        Set<Skill> receiverSkills = new HashSet<>(receiver.getSkills());

        addedSkills.stream()
                .filter(skill -> !receiverSkills.contains(skill))
                .forEach(skill -> {
                    receiver.addSkill(skill);
                    skill.addUser(receiver);
                });
    }

    private void addGuaranteeToReceiverSkillIfAbsent(Recommendation recommendation, List<Skill> addedSkills) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();

        receiver.getSkills().stream()
                .filter(addedSkills::contains)
                .filter(skill -> !skill.userIsGuarantor(author))
                .forEach(skill -> {
                    UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                            .user(receiver)
                            .skill(skill)
                            .guarantor(author)
                            .build();
                    skill.addGuarantee(guarantee);
                });
    }

    private void validateRecommendation(Recommendation newRecommendation) {
        if (!recommendationValidator.isPeriodElapsedSinceLastRecommendation(
                getLastReceiverRecommendationFromAuthor(newRecommendation),
                newRecommendation,
                Duration.ofDays(REQUIRED_DURATION_IN_DAYS))) {
            throw new DataValidationException("Less than 6 months since last referral");
        }
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

        mapToFullSkillOffers(recommendationDto.skillOffers(), recommendation);
        return recommendation;
    }

    private void mapToFullSkillOffers(List<SkillOfferDto> skillOfferDtos, Recommendation recommendation) {
        Queue<Skill> skills = new LinkedList<>(skillService.getSkillsFrom(skillOfferDtos));
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            skillOffer.setRecommendation(recommendation);
            skillOffer.setSkill(skills.poll());
        }
    }

    private SkillOffer mapToFullSkillOffer(SkillOfferDto skillOfferDto, Recommendation recommendation) {
        SkillOffer skillOffer = skillOfferMapper.toEntity(skillOfferDto);
        skillOffer.setSkill(skillService.getSkillById(skillOfferDto.skillId()));
        skillOffer.setRecommendation(recommendation);
        return skillOffer;
    }
}
