package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.MentorshipDto;
import school.faang.user_service.mappers.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MentorshipDto> getMentees(long mentorId) {
        List<MentorshipDto> mentees = new ArrayList<>();
        mentorshipRepository.findById(mentorId).ifPresent(mentor ->
                mentees.addAll(mentor.getMentees().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
        );

        return mentees;
    }

    public List<MentorshipDto> getMentors(long menteeId) {
        List<MentorshipDto> mentors = new ArrayList<>();
        mentorshipRepository.findById(menteeId).ifPresent(mentee ->
                mentors.addAll(mentee.getMentors().stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
        );

        return mentors;
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipRepository.deleteMentorship(menteeId, mentorId);
    }
}

