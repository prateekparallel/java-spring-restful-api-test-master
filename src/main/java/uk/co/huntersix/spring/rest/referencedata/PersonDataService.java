package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonDataService {
	public static final List<Person> PERSON_DATA = new ArrayList<>();

	static {
		PERSON_DATA.add(new Person("Mary", "Smith"));
		PERSON_DATA.add(new Person("Brian", "Archer"));
		PERSON_DATA.add(new Person("Collin", "Brown"));
		PERSON_DATA.add(new Person("Mark", "Smith"));
		PERSON_DATA.add(new Person("Abhijit", "Archer"));
		PERSON_DATA.add(new Person("Collin", "Smith"));
	}

	public Person findPerson(String lastName, String firstName) {

		return PERSON_DATA.stream()
				.filter(p -> p.getFirstName().equalsIgnoreCase(firstName) && p.getLastName().equalsIgnoreCase(lastName))
				.findFirst().map(p -> p).orElse(null);

	}

	public List<Person> findPersonList(String lastName) {

		List<Person> plist = PERSON_DATA.stream().filter(p -> p.getLastName().equalsIgnoreCase(lastName))
				.collect(Collectors.toList());

		if (plist.isEmpty()) {
			return null;
		}

		return plist;

	}

	public HttpStatus register(Person person) {

		if (isDuplicateEntry(person)) {
			return HttpStatus.CONFLICT;
		}

		PERSON_DATA.add(person);
		return HttpStatus.CREATED;

	}
	
	
	public HttpStatus udatePerson(String newFirstName, Person person) {
		Person p = findPerson(person.getLastName(), person.getFirstName());
		//I am not checking for duplicate here as per assignment condition
		if (p != null) {
			p.setFirstName(newFirstName);
			return HttpStatus.NO_CONTENT;
		}
		
		return HttpStatus.NOT_ACCEPTABLE;
	}

	
	public boolean isDuplicateEntry(Person person) {
		Person p = findPerson(person.getLastName(), person.getFirstName());
		if (p != null) {
			return true;
		}
		return false;
	}
	
	public String testStaticMethod(String msg) {
		return Utils.justForTest(msg);
	}
	
	private String privateMethod() {
		return "I am a Private method";
	}
	
	public String testPrivateMethod() {
		return privateMethod();
	}
}
