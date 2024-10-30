package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final int NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;


    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        checkIfOfferedSkillsExist(recommendationDto);
        checkIfAcceptableTimeForRecommendation(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        recommendationDto.setId(recommendationId);
        addSkillOffersAndGuarantee(recommendationDto);

        Recommendation recommendation = recommendationRepository.findById(recommendationDto.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("There is no recommendation with id = %d", recommendationDto.getId())));

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        checkIfOfferedSkillsExist(recommendationDto);
        checkIfAcceptableTimeForRecommendation(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        addSkillOffersAndGuarantee(recommendationDto);

        Recommendation updatedRecommendation = recommendationRepository.findById(recommendationDto.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("There is no recommendation with id = %d", recommendationDto.getId())));

        return recommendationMapper.toDto(updatedRecommendation);
    }

    @Transactional
    public void delete(long id) {
        recommendationRepository.deleteById(id);
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
                .orElseThrow(() -> new NoSuchElementException(String.format("There isn't receiver with id = %d",
                        recommendationDto.getReceiverId())));

        User author = userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new NoSuchElementException(String.format("There isn't author of recommendation with id = %d",
                        recommendationDto.getAuthorId())));

        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build();

        skill.getGuarantees().add(guarantee);
        skillRepository.save(skill);
    }

    private void checkIfAcceptableTimeForRecommendation(RecommendationDto recommendationDto) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId())
                .ifPresent(recommendation -> {
                    if (recommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION))) {
                        throw new DataValidationException(
                                String.format(ErrorMessage.RECOMMENDATION_TIME_LIMIT,
                                        recommendationDto.getAuthorId(),
                                        recommendationDto.getReceiverId(),
                                        NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION));
                    }
                });
    }

    private void checkIfOfferedSkillsExist(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        List<String> skillTitlesList = skillOfferDtoList.stream()
                .map(SkillOfferDto::getSkillTitle)
                .toList();

        for (String skillTitle : skillTitlesList) {
            if (!skillRepository.existsByTitle(skillTitle)) {
                throw new DataValidationException(String.format(ErrorMessage.SKILL_NOT_EXIST, skillTitle));
            }
        }
    }
}
