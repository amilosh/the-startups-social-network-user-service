package school.faang.user_service.service.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.RecommendationValidator;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferService skillOfferService;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserService userService;
    private final RecommendationEventPublisher recommendationEventPublisher;

    public RecommendationDto create(RecommendationDto recommendationDto) throws JsonProcessingException {
        recommendationValidator.checkMainValidation(recommendationDto);
        recommendationValidator.checkRequest(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        skillOfferService.saveSkillOffers(recommendationDto.getSkillOffers(), recommendationId);
        handleGuarantees(recommendationDto);
        recommendationDto.setId(recommendationId);

        recommendationEventPublisher.publish(
                new RecommendationEvent(
                        recommendationDto.getId(), recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId(), recommendationDto.getCreatedAt()
                )
        );

        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationValidator.checkId(recommendationDto);
        recommendationValidator.checkMainValidation(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        skillOfferService.deleteAllByRecommendationId(recommendationDto.getId());
        skillOfferService.saveSkillOffers(recommendationDto.getSkillOffers(), recommendationDto.getId());
        handleGuarantees(recommendationDto);
        return recommendationDto;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recommendationRepository.findAllByReceiverId(userId, pageable)
                .map(recommendationMapper::toDto).stream().toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recommendationRepository.findAllByAuthorId(authorId, pageable)
                .map(recommendationMapper::toDto).stream().toList();
    }

    private void addGuaranteeIfNotGranted(User receiver, User guarantor, Skill skill) {
        if (skill.getGuarantees().stream()
                .filter(guarantee -> Objects.equals(guarantee.getGuarantor().getId(), guarantor.getId())
                        && Objects.equals(guarantee.getUser().getId(), receiver.getId()))
                .findAny().isEmpty()) {
            UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                    .user(receiver)
                    .guarantor(guarantor)
                    .skill(skill)
                    .build();
            guarantee = userSkillGuaranteeRepository.save(guarantee);
            skill.getGuarantees().add(guarantee);
        }
    }

    private void handleGuarantees(RecommendationDto recommendationDto) {
        User receiver = userService.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Receiver not found"));
        User guarantor = userService.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Guarantor not found"));

        List<Long> recommendationSkillIds = recommendationDto.getSkillOffers().stream()
                .mapToLong(SkillOfferDto::getSkillId).boxed().toList();
        List<Skill> userSkills = skillRepository.findAllByUserId(receiver.getId()).stream()
                .filter(skill -> recommendationSkillIds.contains(skill.getId())).toList();
        userSkills.forEach(skill -> addGuaranteeIfNotGranted(receiver, guarantor, skill));
    }
}
