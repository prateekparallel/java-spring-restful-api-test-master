package uk.co.huntersix.spring.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RestController
public class PersonController {
	private PersonDataService personDataService;

	public PersonController(@Autowired PersonDataService personDataService) {
		this.personDataService = personDataService;
	}

	@GetMapping("/person/{lastName}/{firstName}")
	public ResponseEntity<Person> person(@PathVariable(value = "lastName") String lastName,
			@PathVariable(value = "firstName") String firstName) {
		Person p = personDataService.findPerson(lastName, firstName);

		if (p != null) {

			return ResponseEntity.ok().body(p);
		}

		return ResponseEntity.notFound().build();
	}

	
	@GetMapping("/person/{lastName}")
	public ResponseEntity<?> person(@PathVariable(value = "lastName") String lastName) {
		List<Person> pList = personDataService.findPersonList(lastName);

		if (pList != null) {

			return ResponseEntity.ok().body(pList);
		}

		String response_msg = "No records found";
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Records found with lastName :" + lastName);
	}

	
	
	@PostMapping(path="/person",consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> register(@RequestBody Person p) throws Exception {

		if( p == null || p.getFirstName().isEmpty() || p.getLastName().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Not a correct input");
		}
		//we are creating a local person object to avoid side effect
		Person person = new Person(p.getFirstName(), p.getLastName());

		HttpStatus status = personDataService.register(person);

		if (status == HttpStatus.CONFLICT) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Error : Duplicate Entry");
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Registration Successful");
	}
	
	
	@PutMapping(path="/person/{newFirstName}",consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updatePerson(@PathVariable(value = "newFirstName") String newFirstName, @RequestBody Person p) throws Exception {

		if( p == null || p.getFirstName().isEmpty() || p.getLastName().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Not a correct input");
		}
		
		HttpStatus status = personDataService.udatePerson(newFirstName, p);

		if (status == HttpStatus.NOT_ACCEPTABLE) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Error : Person not found");
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}