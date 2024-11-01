package school.faang.user_service.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.recommendation.ServiceRecommendationValidator;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationRepository recommendationRepository;
    private final ServiceRecommendationValidator serviceRecommendationValidator;

    @Value("${settingRecommendationPage.setStartPage}")
    private int page;
    @Value("${settingRecommendationPage.setSize}")
    private int size;

    @Transactional
    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        log.info("Start of recommendation {} processing", recommendationDto);
        serviceRecommendationValidator.checkingThePeriodOfFasting(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        serviceRecommendationValidator.checkingTheSkillsOfRecommendation(recommendationDto.getSkillOffers());
        //checkingTheUserSkills()

        log.info("A recommendation {} is being created", recommendationDto);
        recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        log.info("A recommendation {} has been created", recommendationDto);
        return recommendationDto;
    }

    @Transactional
    public void deleteRecommendation(RecommendationDto delRecommendationDto) {
        log.info("The recommendation {} is being deleted", delRecommendationDto);
        //TODO написать валидацию preparing beforeDelete (userId, recommendationId)
        recommendationRepository.deleteById(delRecommendationDto.getId());
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recommendation> recommendationsPage = recommendationRepository.findAllByReceiverId(receiverId, pageable);

        //TODO написать маппер

        return recommendationsPage.getContent().stream().collect(Collectors.toList());
    }

    @Transactional
    public void updateRecommendation(RecommendationDto updateRecommendationDto) {
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
        //TODO покрыть тестами и добавить гарант
    }
}
