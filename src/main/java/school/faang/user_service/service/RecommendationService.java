package school.faang.user_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mapper.RecommendationMapper;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

@Component
public class RecommendationService {

    public static final int MIN_RECOMMEND_PERIOD = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferService skillOfferService;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository,
            SkillOfferService skillOfferService,
            SkillRepository skillRepository, UserSkillGuaranteeRepository userSkillGuaranteeRepository,
            RecommendationMapper recommendationMapper) {
        this.recommendationRepository = recommendationRepository;
        this.skillOfferService = skillOfferService;
        this.skillRepository = skillRepository;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
        this.recommendationMapper = recommendationMapper;
    }

    private void checkPresenceUserSkills(RecommendationDto recommendation) {
        List<Skill> userSkills = skillRepository.findAllByUserId(recommendation.receiverId());
        List<Long> skillIds = new ArrayList<>();

        if (userSkills != null && !userSkills.isEmpty()) {
            skillIds.addAll(userSkills.stream().map(Skill::getId).toList());
        }

        if (!skillIds.isEmpty() && recommendation.skillOffers() != null) {
            List<Long> skillIdsFromOffer = recommendation.skillOffers().stream().map(SkillOfferDto::skillId).toList();
            skillIdsFromOffer.forEach(skillId -> {
                if (skillIds.contains(skillId)) {
                    List<SkillOffer> skillOfferList = skillOfferService
                            .findAllOffersOfSkillByUserId(skillId, recommendation.receiverId());
                    skillOfferList.forEach(skillOffer -> {
                        if (skillOffer.getRecommendation().getReceiver().getId() != recommendation.receiverId()) {
                            userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                                    .user(skillOffer.getRecommendation().getAuthor())
                                    .skill(skillOffer.getSkill())
                                    .guarantor(skillOffer.getRecommendation().getReceiver())
                                    .build());
                        } else {
                            throw new DataValidationException("This author is already a guarantor of skill " + skillOffer.getSkill().getTitle());
                        }
                    });
                }
            });
        }
    }

    private void validateLastRecommendDate(Long authorId, Long receiverId) {
        LocalDateTime today = LocalDateTime.now();
        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (lastRecommendation.isPresent()) {
            if (!lastRecommendation.get().getCreatedAt().isAfter(today.plusMonths(-MIN_RECOMMEND_PERIOD))) {
                throw new DataValidationException("Last recommendation was earlier than 6 months");
            }
        }
    }

    private void validateSkillsExistenceInDB(List<SkillOfferDto> skillOffers) {
        List<Long> skillIds = skillOffers.stream().map(SkillOfferDto::skillId).toList();

        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new DataValidationException("One or more skills are absent in database");
        }
    }

    public RecommendationDto create(RecommendationDto recommendation) {
        validateLastRecommendDate(recommendation.authorId(), recommendation.receiverId());
        Long recommendId = recommendationRepository.create(recommendation.authorId(), recommendation.receiverId(),
                recommendation.content());

        if (recommendation.skillOffers() != null && !recommendation.skillOffers().isEmpty()) {
            validateSkillsExistenceInDB(recommendation.skillOffers());
            checkPresenceUserSkills(recommendation);
            skillOfferService.saveSkillOffers(recommendation.skillOffers(), recommendId);
        }

        return new RecommendationDto(recommendId, recommendation.authorId(), recommendation.receiverId(),
                recommendation.content(), recommendation.skillOffers(), recommendation.createdAt());
    }

    public RecommendationDto update(RecommendationDto updatedRecommend) {
        validateLastRecommendDate(updatedRecommend.authorId(), updatedRecommend.receiverId());

        if(updatedRecommend.skillOffers() != null && !updatedRecommend.skillOffers().isEmpty()) {
            validateSkillsExistenceInDB(updatedRecommend.skillOffers());
            skillOfferService.deleteAllSkillOffers(updatedRecommend.id());
            skillOfferService.saveSkillOffers(updatedRecommend.skillOffers(), updatedRecommend.id());
        }

        recommendationRepository.update(updatedRecommend.authorId(), updatedRecommend.receiverId(),
                updatedRecommend.content());
        checkPresenceUserSkills(updatedRecommend);
        return updatedRecommend;
    }

    public void delete(long recommendId) {
        recommendationRepository.deleteById(recommendId);
    }

    public List<RecommendationDto> getAllUserRecommendation(long receiverId) {
        Page<Recommendation> allRecommendation = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());

        return allRecommendation.stream().map(recommendationMapper::toDto).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> allGivenRecommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());

        return allGivenRecommendations.stream().map(recommendationMapper::toDto).toList();
    }
}