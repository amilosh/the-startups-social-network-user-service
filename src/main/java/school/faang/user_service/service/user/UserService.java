//package school.faang.user_service.service.user;
//
//
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import school.faang.user_service.entity.User;
//import school.faang.user_service.repository.UserRepository;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    @Transactional
//    public User getUserById(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User with this id does not exist in the database"));
//    }
//}