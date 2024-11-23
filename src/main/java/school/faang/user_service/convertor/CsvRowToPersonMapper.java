package school.faang.user_service.convertor;

import org.springframework.stereotype.Component;
import school.faang.user_service.domain.Address;
import school.faang.user_service.domain.ContactInfo;
import school.faang.user_service.domain.Education;
import school.faang.user_service.domain.Person;
import school.faang.user_service.domain.PreviousEducation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class CsvRowToPersonMapper {
    public Person mapRowToPerson(Map<String, String> row) {
        ContactInfo contactInfo = createContactInfo(row);
        Education education = createEducation(row);
        PreviousEducation previousEducation = createPreviousEducation(row);

        Person person = new Person();
        person.setFirstName(row.get("firstName"));
        person.setLastName(row.get("lastName"));
        person.setYearOfBirth(Integer.parseInt(row.get("yearOfBirth")));
        person.setGroup(row.get("group"));
        person.setStudentID(row.get("studentID"));
        person.setContactInfo(contactInfo);
        person.setEducation(education);
        person.setStatus(row.get("status"));
        person.setAdmissionDate(LocalDate.parse(row.get("admissionDate")));
        person.setGraduationDate(LocalDate.parse(row.get("graduationDate")));
        person.setPreviousEducation(List.of(previousEducation));
        person.setScholarship(Boolean.parseBoolean(row.get("scholarship")));
        person.setEmployer(row.get("employer"));

        return person;
    }

    private ContactInfo createContactInfo(Map<String, String> row) {
        Address address = new Address();
        address.setStreet(row.get("street"));
        address.setCity(row.get("city"));
        address.setState(row.get("state"));
        address.setCountry(row.get("country"));
        address.setPostalCode(row.get("postalCode"));

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(row.get("email"));
        contactInfo.setPhone(row.get("phone"));
        contactInfo.setAddress(address);

        return contactInfo;
    }

    private Education createEducation(Map<String, String> row) {
        Education education = new Education();
        education.setFaculty(row.get("faculty"));
        education.setYearOfStudy(Integer.parseInt(row.get("yearOfStudy")));
        education.setMajor(row.get("major"));

        return education;
    }

    private PreviousEducation createPreviousEducation (Map<String, String> row) {
        PreviousEducation previousEducation = new PreviousEducation();
        previousEducation.setDegree(row.get("degree"));
        previousEducation.setInstitution(row.get("institution"));
        previousEducation.setCompletionYear(Integer.parseInt(row.get("completionYear")));

        return  previousEducation;
    }
}
