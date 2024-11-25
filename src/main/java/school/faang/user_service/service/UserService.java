package school.faang.user_service.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.model.person.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserServiceValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;
    private final UserServiceValidator validator;

    private final CsvMapper csvMapper;

    private final CountryService countryService;

    public List<UserDto> uploadCsvUsers(MultipartFile file) {
        List<Person> persons = fromCsv(file);

        List<User> users = personMapper.toUsers(persons).stream()
                .peek(user -> {
                    user.setActive(true);
                    user.setPassword("p" + ThreadLocalRandom.current().nextInt(9999, 9999999 + 1));
                    if (user.getCountry() != null && user.getCountry().getTitle() != null) {
                        user.setCountry(countryService.getOrCreateCountry(user.getCountry().getTitle()));
                    }
                })
                .toList();

        return userMapper.toDto(userRepository.saveAll(users));
    }

    private List<Person> fromCsv(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {

            MappingIterator<Person> personIterator = csvMapper.readerFor(Person.class)
                    .with(CsvSchema.emptySchema().withHeader())
                    .readValues(inputStream);

            return personIterator.readAll().stream()
                    .peek(validator::validatePerson)
                    .toList();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while reading file", e);
        }
    }

    public UserDto getUserDtoById(Long id) {
        return userMapper.toDto(getUserById(id));
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", id)));
    }
}
