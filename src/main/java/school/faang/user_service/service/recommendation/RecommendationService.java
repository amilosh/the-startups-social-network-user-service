package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.validation.RecommendationValidation;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferService skillOfferService;
    private final RecommendationValidation recommendationValidation;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserService userService;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidation.checkTimeInterval(recommendationDto);
        recommendationValidation.checkSkillsExist(recommendationDto);
        recommendationValidation.checkSkillsUnique(recommendationDto);
        recommendationValidation.checkRequest(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        skillOfferService.saveSkillOffers(recommendationDto.getSkillOffers(), recommendationId);
        hanldeGuarantees(recommendationDto);
        recommendationDto.setId(recommendationId);
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationValidation.checkId(recommendationDto);
        recommendationValidation.checkTimeInterval(recommendationDto);
        recommendationValidation.checkSkillsExist(recommendationDto);
        recommendationValidation.checkSkillsUnique(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        skillOfferService.deleteAllByRecommendationId(recommendationDto.getId());
        skillOfferService.saveSkillOffers(recommendationDto.getSkillOffers(), recommendationDto.getId());
        hanldeGuarantees(recommendationDto);
        return recommendationDto;
    }

    public boolean recommendationExists(long id) {
        return recommendationRepository.findById(id).isPresent();
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
                .filter(guarantee -> Objects.equals(guarantee.getGuarantor().getId(), guarantor.getId()))
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

    private void hanldeGuarantees(RecommendationDto recommendationDto) {
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
