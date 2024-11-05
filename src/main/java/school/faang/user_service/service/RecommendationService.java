package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import school.faang.user_service.mapper.RecommendationListMapper;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferListMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationListMapper recommendationListMapper;
    private final SkillOfferListMapper skillOfferListMapper;
    private final RecommendationValidator recommendationValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        Long recommendationId = recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        List<Skill> receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());

        User author = getAuthor(recommendation.getAuthorId());
        User receiver = getAuthor(recommendation.getReceiverId());

        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
            updateGuarantee(receiverSkills, skillOffer.getSkillId(), author, receiver);

            skillOfferRepository.create(skillOffer.getSkillId(), recommendationId);
        }

        Recommendation recommendationSaved = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException(String.format("Recommendation id = %d not found", recommendationId)));

        return toRecommendationDto(recommendationSaved);
    }

    @Transactional
    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateRecommendationExist(recommendation);
        validateRecommendation(recommendation);

        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        List<Skill> receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());

        User author = getAuthor(recommendation.getAuthorId());
        User receiver = getAuthor(recommendation.getReceiverId());

        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
            updateGuarantee(receiverSkills, skillOffer.getSkillId(), author, receiver);

            skillOfferRepository.create(skillOffer.getSkillId(), recommendation.getId());
        }

        Recommendation recommendationSaved = recommendationRepository.findById(recommendation.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Recommendation id = %d not found", recommendation.getId())));

        return toRecommendationDto(recommendationSaved);
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
        return toRecommendationDtoList(page.getContent());
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        recommendationValidator.validateAuthorExist(authorId);

        Page<Recommendation> page = recommendationRepository.findAllByAuthorId(authorId, PageRequest.of(0, 200));
        return toRecommendationDtoList(page.getContent());
    }

    private List<RecommendationDto> toRecommendationDtoList(List<Recommendation> recommendations) {
        List<RecommendationDto> recommendationDtos = recommendationListMapper.toRecommendationDtoList(recommendations);
        for (RecommendationDto recommendationDto : recommendationDtos) {
            Optional<Recommendation> foundRecommendation = recommendations.stream().filter(r -> r.getId() == recommendationDto.getId()).findFirst();
            foundRecommendation.ifPresent(r -> {
                if (r.getSkillOffers() != null) {
                    recommendationDto.setSkillOffers(skillOfferListMapper.toSkillOfferDtoList(r.getSkillOffers()));
                }
            });
        }

        return recommendationDtos;
    }

    private RecommendationDto toRecommendationDto(Recommendation recommendation) {
        RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);
        if (recommendation.getSkillOffers() != null) {
            recommendationDto.setSkillOffers(skillOfferListMapper.toSkillOfferDtoList(recommendation.getSkillOffers()));
        }
        return recommendationDto;
    }

    private void updateGuarantee(List<Skill> receiverSkills, Long skillIdFromOffer, User author, User receiver) {
        Optional<Skill> skillOptional = receiverSkills.stream().filter(skill -> skill.getId() == skillIdFromOffer).findFirst();

        skillOptional.ifPresent(skill -> {
            boolean existGuarantee = skill.getGuarantees().stream().anyMatch(g -> g.getGuarantor().getId().equals(author.getId()));
            if (existGuarantee) {
                skill.getGuarantees().add(new UserSkillGuarantee() {{
                    setUser(receiver);
                    setGuarantor(author);
                    setSkill(skill);
                }});
                skillRepository.save(skill);
            }
        });
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
                .orElseThrow(() -> new RuntimeException(String.format("Author with id = %s not found", authorId)));
    }

    private User getReceiver(Long receiverId) {
        return userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException(String.format("Receiver with id = %s not found", receiverId)));
    }
}