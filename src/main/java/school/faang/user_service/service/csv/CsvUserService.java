package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Person;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.person.PersonMapper;
import school.faang.user_service.repository.CountryRepository;
import org.springframework.transaction.PlatformTransactionManager;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvUserService {
    private final PersonMapper personMapper;
    private final CountryRepository countryRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManager entityManager;
    private final UserRepository userRepository;


    public ResponseEntity<String> getStudentsParsing(MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();

            CsvMapper csvMapper = new CsvMapper();
            CsvSchema csvSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');

            MappingIterator<Person> mappingIterator = csvMapper.readerFor(Person.class).with(csvSchema).readValues(inputStream);
            List<Person> persons = mappingIterator.readAll();

            List<User> users = new ArrayList<>();
            for (Person person : persons) {
                ContactInfo contactInfo = new ContactInfo();
                Address address = new Address();
                contactInfo.setEmail((String) person.getAdditionalProperties().get("email"));
                contactInfo.setPhone((String) person.getAdditionalProperties().get("phone"));
                address.setCountry((String) person.getAdditionalProperties().get("city"));
                address.setCity((String) person.getAdditionalProperties().get("country"));
                contactInfo.setAddress(address);
                person.setContactInfo(contactInfo);
                users.add(personMapper.personToUser(person));
            }
            List<String> titleList = users.stream().map(user -> user.getCountry().getTitle()).toList();
            List<Country> countryList = countryRepository.findByT(titleList);

            for (User user : users) {
                String countryTitle = user.getCountry().getTitle();
                user.setPassword("111");
                Country countryResult = countryList.stream()
                        .filter(country -> country.getTitle().contains(countryTitle))
                        .findFirst()
                        .orElseGet(() -> {
                            Country newCountry = new Country();
                            newCountry.setTitle(countryTitle);
                            return countryRepository.save(newCountry);
                        });
                user.setCountry(countryResult);
            }

            batchInsertUsers(users);

            return ResponseEntity.ok("Users uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading users");
        }
    }

    private void batchInsertUsers(List<User> users) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                boolean existsByResult = userRepository.existsByUsernameOrEmailOrPhone(user.getUsername(),
                        user.getEmail(), user.getPhone());

                validateUserBeforeSave(user, existsByResult);

                if (!existsByResult ) {
                    entityManager.persist(user);
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private void validateUserBeforeSave(User user, boolean existsByResult) {
        if (existsByResult) {
            log.warn("User {} already exists", user.getUsername());
        }
    }
}