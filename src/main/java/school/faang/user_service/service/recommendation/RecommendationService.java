package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationDtoValidator;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationDtoValidator recommendationDtoValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        log.info("Creating a recommendation from user with id {} for user with id {}",
                recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        recommendationDtoValidator.validateRecommendation(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        recommendationDto.setId(recommendationId);
        addSkillOffersAndGuarantee(recommendationDto);
        log.info("Recommendation with id {} successfully saved", recommendationId);

        return recommendationMapper.toDto(getRecommendation(recommendationDto.getId()));
    }

    @Transactional
    public RecommendationDto update(Long id, RecommendationDto recommendationDto) {
        log.info("Updating recommendation with id {}", id);

        recommendationDto.setId(id);
        recommendationDtoValidator.validateRecommendation(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        addSkillOffersAndGuarantee(recommendationDto);
        log.info("Recommendation with id {} successfully updated", id);

        return recommendationMapper.toDto(getRecommendation(recommendationDto.getId()));
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting recommendation with id {}", id);
        recommendationRepository.deleteById(id);
        log.info("Recommendation with id {} successfully deleted", id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        log.info("Getting all recommendations for user with id {}", receiverId);
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        log.debug("Found {} recommendations for user with id {}", recommendations.getTotalElements(), receiverId);
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        log.info("Getting all recommendations created by user with id {}", authorId);
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        log.debug("User with id {} created {} recommendations", authorId, recommendations.getTotalElements());
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    private Recommendation getRecommendation(Long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> {
                    log.error("Recommendation with id {} not found", recommendationId);
                    return new NoSuchElementException(
                            String.format("There is no recommendation with id = %d", recommendationId));
                });
    }

    private void addSkillOffersAndGuarantee(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationDto.getId());
            skillRepository.findUserSkill(skillOfferDto.getSkillId(), recommendationDto.getReceiverId())
                    .ifPresent(skill -> {
                        if (!isAuthorAlreadyGuarantor(recommendationDto, skill)) {
                            addGuaranteeToSkill(recommendationDto, skill);
                        }
                    });
        }
    }

    private boolean isAuthorAlreadyGuarantor(RecommendationDto recommendationDto, Skill skill) {
        return skill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId)
                .anyMatch(recommendationDto.getAuthorId()::equals);
    }

    private void addGuaranteeToSkill(RecommendationDto recommendationDto, Skill skill) {
        User receiver = userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> {
                    log.error("Receiver with id {} not found", recommendationDto.getReceiverId());
                    return new NoSuchElementException(String.format("There isn't receiver with id = %d",
                            recommendationDto.getReceiverId()));
                });

        User author = userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author with id {} not found", recommendationDto.getAuthorId());
                    return new NoSuchElementException(String.format("There isn't author of recommendation with id = %d",
                            recommendationDto.getAuthorId()));
                });

        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build();

        skill.getGuarantees().add(guarantee);
        skillRepository.save(skill);
    }
}
