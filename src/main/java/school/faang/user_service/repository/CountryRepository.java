package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Country;

import java.util.Optional;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM country WHERE title = ?1")
    Optional<Country> findByTitle (String title);
}