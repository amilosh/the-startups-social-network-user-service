package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.RequestSkillOfferDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
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
    public ResponseRecommendationDto create(RequestRecommendationDto requestRecommendationDto) {
        log.info("Creating a recommendation from user with id {} for user with id {}",
                requestRecommendationDto.getAuthorId(), requestRecommendationDto.getReceiverId());

        recommendationDtoValidator.validateRecommendation(requestRecommendationDto);

        Long recommendationId = recommendationRepository.create(
                requestRecommendationDto.getAuthorId(),
                requestRecommendationDto.getReceiverId(),
                requestRecommendationDto.getContent());

        requestRecommendationDto.setId(recommendationId);
        addSkillOffersAndGuarantee(requestRecommendationDto);
        log.info("Recommendation with id {} successfully saved", recommendationId);

        return recommendationMapper.toDto(getRecommendation(requestRecommendationDto.getId()));
    }

    @Transactional
    public ResponseRecommendationDto update(Long id, RequestRecommendationDto requestRecommendationDto) {
        log.info("Updating recommendation with id {}", id);

        requestRecommendationDto.setId(id);
        recommendationDtoValidator.validateRecommendation(requestRecommendationDto);

        recommendationRepository.update(
                requestRecommendationDto.getAuthorId(),
                requestRecommendationDto.getReceiverId(),
                requestRecommendationDto.getContent());

        skillOfferRepository.deleteAllByRecommendationId(requestRecommendationDto.getId());
        addSkillOffersAndGuarantee(requestRecommendationDto);
        log.info("Recommendation with id {} successfully updated", id);

        return recommendationMapper.toDto(getRecommendation(requestRecommendationDto.getId()));
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
                    log.error("Recommendation with id {} not found", recommendationId);
                    return new NoSuchElementException(
                            String.format("There is no recommendation with id = %d", recommendationId));
                });
    }

    private void addSkillOffersAndGuarantee(RequestRecommendationDto requestRecommendationDto) {
        List<RequestSkillOfferDto> requestSkillOfferDtoList = requestRecommendationDto.getSkillOffers();
        if (requestSkillOfferDtoList == null || requestSkillOfferDtoList.isEmpty()) {
            log.debug("No skill offers to process for recommendation with id {}", requestRecommendationDto.getId());
            return;
        }

        for (RequestSkillOfferDto requestSkillOfferDto : requestSkillOfferDtoList) {
            skillOfferRepository.create(requestSkillOfferDto.getSkillId(), requestRecommendationDto.getId());
            skillRepository.findUserSkill(requestSkillOfferDto.getSkillId(), requestRecommendationDto.getReceiverId())
                    .ifPresent(skill -> {
                        if (!isAuthorAlreadyGuarantor(requestRecommendationDto, skill)) {
                            addGuaranteeToSkill(requestRecommendationDto, skill);
                            log.debug("Added guarantee for skill '{}' to user '{}'", skill.getTitle(), requestRecommendationDto.getReceiverId());
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
        User receiver = userRepository.findById(requestRecommendationDto.getReceiverId())
                .orElseThrow(() -> {
                    log.error("Receiver with id {} not found", requestRecommendationDto.getReceiverId());
                    return new NoSuchElementException(String.format("There isn't receiver with id = %d",
                            requestRecommendationDto.getReceiverId()));
                });

        User author = userRepository.findById(requestRecommendationDto.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author with id {} not found", requestRecommendationDto.getAuthorId());
                    return new NoSuchElementException(String.format("There isn't author of recommendation with id = %d",
                            requestRecommendationDto.getAuthorId()));
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
