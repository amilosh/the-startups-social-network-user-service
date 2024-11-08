package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationDtoValidator;

import java.util.List;
import java.util.stream.Stream;

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


    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        List<Recommendation> recommendations = recommendationRepository.findListByReceiverId(receiverId);
        log.info("Found {} recommendations for user with id - {}", recommendations.size(), receiverId);
        return recommendationMapper.toDtoList(recommendations);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        List<Recommendation> recommendations = recommendationRepository.findListByAuthorId(authorId);
        log.info("User with id - {} created {} recommendations", authorId, recommendations.size());
        return recommendationMapper.toDtoList(recommendations);
    }

    @Transactional
    public RecommendationDto create(RecommendationDto recDto) {
        recommendationDtoValidator.validateExistedSkillsAndDate(recDto);
        addSkillOffersAndGuarantee(recDto);
        Recommendation result = recommendationRepository.save(recommendationMapper.toEntity(recDto));
        log.info("Recommendation with id - {} successfully saved", result.getId());

        return recommendationMapper.toDto(result);

    }

    @Transactional
    public RecommendationDto update(RecommendationDto recDto) {
        recommendationDtoValidator.validateExistedSkillsAndDate(recDto);
        addSkillOffersAndGuarantee(recDto);
        log.info("Updating recommendation with id - {}", recDto.getId());

        skillOfferRepository.deleteAllByRecommendationId(recDto.getId());
        Recommendation result = recommendationRepository.save(recommendationMapper.toEntity(recDto));

        return recommendationMapper.toDto(result);
    }

    @Transactional
    public void delete(long id) {
        recommendationRepository.deleteById(id);
        log.info("Recommendation successfully deleted - {}", id);
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
        Stream<Long> authorIdStream = skill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId);

        if (authorIdStream.noneMatch(recDto.getAuthorId()::equals)) {
            User receiver = userRepository.findById(recDto.getReceiverId())
                    .orElseThrow(() -> {
                        log.error("Receiver with id - {} not found!", recDto.getReceiverId());
                        return new RuntimeException(ErrorMessage.RECOMMENDATION_RECEIVER_NOT_FOUND +
                                recDto.getReceiverId());
                    });

            User author = userRepository.findById(recDto.getAuthorId())
                    .orElseThrow(() -> {
                        log.error("Author with id - {} not found!", recDto.getAuthorId());
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
}
