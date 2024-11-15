package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationListMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    public final static String AUTHOR_NOT_FOUND = "Author with id = %s not found";
    public final static String RECEIVER_NOT_FOUND = "Receiver with id = %s not found";
    public final static String RECOMMENDATION_NOT_FOUND = "Recommendation id = %d not found";

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationListMapper recommendationListMapper;
    private final RecommendationValidator recommendationValidator;

    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        Long recommendationId = recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());

        return updateByChangeRecommendation(recommendation, recommendationId);
    }

    @Transactional
    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateRecommendationExist(recommendation);
        validateRecommendation(recommendation);

        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        return updateByChangeRecommendation(recommendation, recommendation.getId());
    }

    @Transactional
    public void deleteRecommendation(long id) {
        recommendationValidator.validateRecommendationExist(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        recommendationValidator.validateReceiverExist(receiverId);

        Page<Recommendation> page = recommendationRepository.findAllByReceiverId(receiverId, PageRequest.of(0, 200));
        return recommendationListMapper.toRecommendationDtoList(page.getContent());
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        recommendationValidator.validateAuthorExist(authorId);

        Page<Recommendation> page = recommendationRepository.findAllByAuthorId(authorId, PageRequest.of(0, 200));
        return recommendationListMapper.toRecommendationDtoList(page.getContent());
    }

    private void updateGuarantee(RecommendationDto recommendation) {
        User author = getAuthor(recommendation.getAuthorId());
        User receiver = getReceiver(recommendation.getReceiverId());
        List<Skill> receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
        List<Recommendation> recommendations = recommendationRepository.findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(author.getId(), receiver.getId());

        List<Long> authorRecommendedSkillIds = recommendations.stream()
                .flatMap(r -> r.getSkillOffers().stream())
                .map(s -> s.getSkill().getId())
                .distinct()
                .toList();

        Set<Long> recommendedSkills = receiverSkills.stream()
                .map(Skill::getId)
                .distinct()
                .filter(authorRecommendedSkillIds::contains)
                .collect(Collectors.toSet());

        recommendation.getSkillOffers().forEach(skillOffer -> {
            Optional<Skill> skillOptional = receiverSkills.stream().filter(skill -> skill.getId() == skillOffer.getSkillId()).findFirst();

            skillOptional.ifPresent(skill -> {
                if (recommendedSkills.contains(skill.getId())) {
                    skill.getGuarantees().add(new UserSkillGuarantee() {{
                        setUser(receiver);
                        setGuarantor(author);
                        setSkill(skill);
                    }});
                    skillRepository.save(skill);
                }
            });
        });
    }

    private void createSkillOffers(RecommendationDto recommendation, Long recommendationId) {
        recommendation.getSkillOffers().forEach(skillOffer -> {
            skillOfferRepository.create(skillOffer.getSkillId(), recommendationId);
        });
    }

    private RecommendationDto updateByChangeRecommendation(RecommendationDto recommendation, Long recommendationId) {
        updateGuarantee(recommendation);
        createSkillOffers(recommendation, recommendationId);

        Recommendation recommendationSaved = recommendationRepository.findById(recommendation.getId())
                .orElseThrow(() -> new DataValidationException(String.format(RECOMMENDATION_NOT_FOUND, recommendation.getId())));

        return recommendationMapper.toRecommendationDto(recommendationSaved);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateAuthorExist(recommendation.getAuthorId());
        recommendationValidator.validateReceiverExist(recommendation.getReceiverId());
        recommendationValidator.validatePeriod(recommendation.getAuthorId(), recommendation.getReceiverId());
        if (recommendation.getSkillOffers() != null) {
            recommendationValidator.validateSkills(recommendation.getSkillOffers().stream().map(SkillOfferDto::getSkillId));
        }
    }

    private User getAuthor(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new DataValidationException(String.format(AUTHOR_NOT_FOUND, authorId)));
    }

    private User getReceiver(Long receiverId) {
        return userRepository.findById(receiverId)
                .orElseThrow(() -> new DataValidationException(String.format(RECEIVER_NOT_FOUND, receiverId)));
    }
}