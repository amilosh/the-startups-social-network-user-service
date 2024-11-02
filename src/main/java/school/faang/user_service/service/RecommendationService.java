package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private static final int PAGE_SIZE = 10;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationServiceValidator recommendationServiceValidator;
    private final UserValidator userValidator;
    private final UserService userService;
    private final SkillOfferService skillOfferService;
    private final SkillService skillService;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationServiceValidator.validateSkillAndTimeRequirementsForGuarantee(recommendationDto);

        Recommendation recommendation = recommendationRepository.save(createRecommendationFromDto(recommendationDto));

        skillOfferService.saveSkillOffers(recommendation);
        skillService.addGuarantee(recommendationDto);

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationServiceValidator.validateSkillAndTimeRequirementsForGuarantee(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        Recommendation recommendation = getRecommendationById(recommendationDto.getId());
        skillOfferService.deleteAllByRecommendationId(recommendation.getId());
        skillOfferService.saveSkillOffers(recommendation);
        skillService.addGuarantee(recommendationDto);

        return recommendationMapper.toDto(getRecommendationById(recommendationDto.getId()));
    }

    public boolean delete(long id) {
        recommendationServiceValidator.validateRecommendationExistsById(id);
        recommendationRepository.deleteById(id);
        return !recommendationRepository.existsById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        userValidator.validateUserById(receiverId);
        List<Recommendation> allRecommendations = getAllRecommendationsByReceiverId(receiverId);
        return recommendationMapper.toDtoList(allRecommendations);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        userValidator.validateUserById(authorId);
        List<Recommendation> allRecommendations = getAllRecommendationsByAuthorId(authorId);
        return recommendationMapper.toDtoList(allRecommendations);
    }

    public Recommendation getRecommendationById(Long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElseThrow(() ->
                new EntityNotFoundException("Recommendation with id #" + recommendationId + " not found"));
    }

    public Recommendation createRecommendationFromDto(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setAuthor(userService.findUserById(recommendationDto.getAuthorId()));
        recommendation.setReceiver(userService.findUserById(recommendationDto.getReceiverId()));
        recommendation.setSkillOffers(skillOfferService.findAllByUserId(recommendationDto.getReceiverId()));
        return recommendation;
    }

    public boolean checkIfRecommendationExistsById(Long recommendationId) {
        return recommendationRepository.existsById(recommendationId);
    }

    private List<Recommendation> getAllRecommendationsByReceiverId(long receiverId) {
        int startPage = 0;
        List<Recommendation> allRecommendations = new ArrayList<>();
        Page<Recommendation> page;

        do {
            page = recommendationRepository.findAllByReceiverId(receiverId, PageRequest.of(startPage, PAGE_SIZE));
            allRecommendations.addAll(page.getContent());
            startPage++;
        } while (page.hasNext());

        return allRecommendations;
    }

    private List<Recommendation> getAllRecommendationsByAuthorId(long authorId) {
        int startPage = 0;
        List<Recommendation> allRecommendations = new ArrayList<>();
        Page<Recommendation> page;

        do {
            page = recommendationRepository.findAllByAuthorId(authorId, PageRequest.of(startPage, PAGE_SIZE));
            allRecommendations.addAll(page.getContent());
            startPage++;
        } while (page.hasNext());

        return allRecommendations;
    }


}
