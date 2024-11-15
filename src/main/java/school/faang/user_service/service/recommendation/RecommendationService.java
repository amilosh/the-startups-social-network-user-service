package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.recommendation.ErrorMessage;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;

    @Transactional
    public ResponseRecommendationDto create(RequestRecommendationDto requestRecommendationDto) {
        log.info("Creating a recommendation from user with id {} for user with id {}",
                requestRecommendationDto.getAuthorId(), requestRecommendationDto.getReceiverId());

        recommendationValidator.validateRecommendation(requestRecommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(requestRecommendationDto);
        addGuarantees(requestRecommendationDto);
        recommendation = recommendationRepository.save(recommendation);

        log.info("Recommendation with id {} successfully saved", recommendation.getId());

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public ResponseRecommendationDto update(Long id, RequestRecommendationDto requestRecommendationDto) {
        log.info("Updating recommendation with id {}", id);

        recommendationValidator.validateRecommendation(requestRecommendationDto);
        Recommendation existingRecommendation = getRecommendation(id);
        recommendationMapper.updateFromDto(requestRecommendationDto, existingRecommendation);
        addGuarantees(requestRecommendationDto);

        existingRecommendation = recommendationRepository.save(existingRecommendation);
        log.info("Recommendation with id {} successfully updated", id);

        return recommendationMapper.toDto(existingRecommendation);
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting recommendation with id {}", id);
        recommendationRepository.deleteById(id);
        log.info("Recommendation with id {} successfully deleted", id);
    }

    public List<ResponseRecommendationDto> getAllUserRecommendations(long receiverId) {
        log.info("Getting all recommendations for user with id {}", receiverId);
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        log.debug("Found {} recommendations for user with id {}", recommendations.getTotalElements(), receiverId);
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    public List<ResponseRecommendationDto> getAllGivenRecommendations(long authorId) {
        log.info("Getting all recommendations created by user with id {}", authorId);
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        log.debug("User with id {} created {} recommendations", authorId, recommendations.getTotalElements());
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    private Recommendation getRecommendation(Long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> {
                    log.warn("Recommendation with id {} not found", recommendationId);
                    return new NoSuchElementException(
                            String.format("There is no recommendation with id = %d", recommendationId));
                });
    }

    private Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.warn("Skill with id {} not found", skillId);
                    return new NoSuchElementException(
                            String.format("There is no skill with id = %d", skillId));
                });
    }

    private void addGuarantees(RequestRecommendationDto requestRecommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = requestRecommendationDto.getSkillOffers();

        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            log.warn("No skill offers to process for recommendation with id {}", requestRecommendationDto.getId());
            throw new DataValidationException(ErrorMessage.NO_SKILL_OFFERS);
        }

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            Skill skill = getSkill(skillOfferDto.getSkillId());

            skillRepository.findUserSkill(skillOfferDto.getSkillId(), requestRecommendationDto.getReceiverId())
                    .ifPresent(existingSkill -> {
                        if (!isAuthorAlreadyGuarantor(requestRecommendationDto, existingSkill)) {
                            addGuaranteeToSkill(requestRecommendationDto, existingSkill);
                            log.debug("Added guarantee for skill '{}' to user '{}'",
                                    skill.getTitle(), requestRecommendationDto.getReceiverId());
                        }
                    });
        }
    }

    private boolean isAuthorAlreadyGuarantor(RequestRecommendationDto requestRecommendationDto, Skill skill) {
        return skill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId)
                .anyMatch(requestRecommendationDto.getAuthorId()::equals);
    }

    private void addGuaranteeToSkill(RequestRecommendationDto requestRecommendationDto, Skill skill) {
        User receiver = recommendationValidator.validateUser(requestRecommendationDto.getReceiverId());
        User author = recommendationValidator.validateUser(requestRecommendationDto.getAuthorId());

        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build();

        skill.getGuarantees().add(guarantee);
        skillRepository.save(skill);
    }
}
