package school.faang.user_service.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final SkillOfferService skillOfferService;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationRepository recommendationRepository;
    private final ServiceRecommendationValidator serviceRecommendationValidator;

    public Optional<Recommendation> findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(long authorId, long receiverId) {
        return recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
    }

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        log.info("Start of recommendation {} processing", recommendationDto);
        serviceRecommendationValidator.checkingThePeriodOfFasting(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        serviceRecommendationValidator.checkingTheSkillsOfRecommendation(recommendationDto.getSkillOffers());
        serviceRecommendationValidator.checkingTheUserSkills(recommendationDto);

        log.info("A recommendation {} is being created", recommendationDto);
        recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        log.info("A recommendation {} has been created", recommendationDto);
        return recommendationDto;
    }

    public void deleteRecommendation(RecommendationDto delRecommendationDto) {
        log.info("The recommendation {} is being deleted", delRecommendationDto);
        serviceRecommendationValidator.preparingBeforeDelete(delRecommendationDto);
        recommendationRepository.deleteById(delRecommendationDto.getId());
        log.info("The recommendation {} has been deleted", delRecommendationDto);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Pageable pageable = Pageable.unpaged();
        Page<Recommendation> recommendationsPage = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return recommendationsPage.getContent().stream()
                .map(recommendationMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationDto updateRecommendation(RecommendationDto updateRecommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(updateRecommendationDto);
        log.info("Start of recommendation {} processing", updateRecommendationDto);
        serviceRecommendationValidator.checkingThePeriodOfFasting(
                updateRecommendationDto.getAuthorId(),
                updateRecommendationDto.getReceiverId());
        serviceRecommendationValidator.checkingTheSkillsOfRecommendation(updateRecommendationDto.getSkillOffers());

        log.info("A recommendation {} is being updated", updateRecommendationDto);
        recommendationRepository.update(
                updateRecommendationDto.getAuthorId(),
                updateRecommendationDto.getReceiverId(),
                updateRecommendationDto.getContent());

        skillOfferService.deleteAllByRecommendationId(recommendation.getId());

        serviceRecommendationValidator.checkingTheUserSkills(updateRecommendationDto);

        return updateRecommendationDto;
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Pageable pageable = Pageable.unpaged();
        Page<Recommendation> recommendationsPage = recommendationRepository.findAllByAuthorId(authorId, pageable);
        return recommendationsPage.getContent().stream()
                .map(recommendationMapper::toDto)
                 .toList();
    }
}

