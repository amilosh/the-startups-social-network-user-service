package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final int DIFFERENCE_BETWEEN_DATE_IN_MONTH = 6;
    private final RecommendationRepository recRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recMapper;


    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        log.info("Found " + recommendations.getTotalElements() + " recommendations for user with id - " + receiverId);
        return recMapper.toDtoList(recommendations.getContent());
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendations = recRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        log.info("User with id - " + authorId + " created " + recommendations.getTotalElements() + " recommendations");
        return recMapper.toDtoList(recommendations.getContent());
    }

    @Transactional
    public RecommendationDto create(RecommendationDto recDto) {
        isDateTimeRecommendationOlderSixMonth(recDto);
        isSkillOfferExists(recDto);
        addSkillOffersAndGuarantee(recDto);
        Recommendation result = recRepository.save(recMapper.toEntity(recDto));
        log.info("Recommendation with id - " + result.getId() + "successfully saved");

        return recMapper.toDto(result);

    }

    @Transactional
    public RecommendationDto update(RecommendationDto requestRecDto) {
        isDateTimeRecommendationOlderSixMonth(requestRecDto);
        isSkillOfferExists(requestRecDto);
        addSkillOffersAndGuarantee(requestRecDto);
        log.info("Updating recommendation with id - " + requestRecDto.getId());

        skillOfferRepository.deleteAllByRecommendationId(requestRecDto.getId());
        Recommendation result = recRepository.save(recMapper.toEntity(requestRecDto));

        return recMapper.toDto(result);
    }

    @Transactional
    public void delete(long id) {
        recRepository.deleteById(id);
        log.info("Recommendation successfully deleted - " + id);
    }

    private void addSkillOffersAndGuarantee(RecommendationDto recDto) {
        if (!recDto.getSkillOffers().isEmpty()) {
            for (SkillOfferDto skillOfferDto : recDto.getSkillOffers()) {
                skillOfferRepository.create(skillOfferDto.getSkillId(), recDto.getId());
                skillRepository.findUserSkill(skillOfferDto.getSkillId(), recDto.getReceiverId())
                        .ifPresent(skill -> addGuaranteeToSkill(recDto, skill));
            }
        }
    }

    private void addGuaranteeToSkill(RecommendationDto recDto, Skill skill) {
        if (skill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId)
                .noneMatch(recDto.getAuthorId()::equals)) {
            User receiver = userRepository.findById(recDto.getReceiverId())
                    .orElseThrow(() -> {
                        log.error("Receiver with id - " + recDto.getReceiverId() + " not found!");
                        return new RuntimeException(ErrorMessage.RECOMMENDATION_RECEIVER_NOT_FOUND +
                                recDto.getReceiverId());
                    });

            User author = userRepository.findById(recDto.getAuthorId())
                    .orElseThrow(() -> {
                        log.error("Author with id - " + recDto.getAuthorId() + " not found!");
                        return new RuntimeException(ErrorMessage.RECOMMENDATION_AUTHOR_NOT_FOUND +
                                recDto.getAuthorId());
                    });

            skill.getGuarantees().add(UserSkillGuarantee.builder()
                    .user(receiver)
                    .skill(skill)
                    .guarantor(author)
                    .build());
            skillRepository.save(skill);
        }
    }

    private void isSkillOfferExists(RecommendationDto recDto) {
        if (!recDto.getSkillOffers().isEmpty()) {
            List<String> skillTitlesList = recDto.getSkillOffers().stream()
                    .map(SkillOfferDto::getSkillTitle)
                    .toList();

            for (String skillTitle : skillTitlesList) {
                if (!skillRepository.existsByTitle(skillTitle)) {
                    log.error("Skill with title - " + skillTitle + " does not exist in the system!", skillTitle);
                    throw new DataValidationException(String.format(ErrorMessage.SKILL_NOT_EXIST, skillTitle));
                }
            }
        }
    }

    private void isDateTimeRecommendationOlderSixMonth(RecommendationDto recDto) {
        recRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recDto.getAuthorId(),
                recDto.getReceiverId()).ifPresent(recommendation -> {
            if (recommendation.getCreatedAt().isAfter(recDto.getCreatedAt().minusMonths(DIFFERENCE_BETWEEN_DATE_IN_MONTH))) {
                throw new DataValidationException(String.format(ErrorMessage.RECOMMENDATION_WRONG_TIME,
                        recDto.getAuthorId(), recDto.getReceiverId(), DIFFERENCE_BETWEEN_DATE_IN_MONTH));
            }
        });
    }
}
