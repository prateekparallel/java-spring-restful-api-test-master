package uk.co.huntersix.spring.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.MyList;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonDataService personDataService;
	
	@MockBean
	private MyList myLIst;

	@Test
	public void shouldReturnPersonFromService() throws Exception {
		when(personDataService.findPerson(any(), any())).thenReturn(new Person("Mary", "Smith"));
		this.mockMvc.perform(get("/person/any/any")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("id").exists()).andExpect(jsonPath("firstName").value("Mary"))
				.andExpect(jsonPath("lastName").value("Smith"));
	}

	@Test
	public void shouldReturnNotFoundStatusWhenPersonNotFound() throws Exception {
		when(personDataService.findPerson(any(), any())).thenReturn(null);
		this.mockMvc.perform(get("/person/smith/mary")).andDo(print()).andExpect(status().isNotFound());
	}

	/*
	 * @Test public void shouldReturnNotFoundStatusWhenFirstNameNotProvided() throws
	 * Exception { when(personDataService.findPerson(any(),
	 * any())).thenReturn(null); this.mockMvc.perform(get("/person/dutta"))
	 * .andDo(print()) .andExpect(status().isNotFound()); }
	 */

	@Test
	public void shouldReturnListOfPerson() throws Exception {

		// List<Person> personList = new ArrayList<Person>();

		// personList.add(new Person("Collin", "Smith"));
		// personList.add(new Person("Mark", "Smith"));

		when(personDataService.findPersonList(any())).thenReturn(
				Stream.of(new Person("Collin", "Smith"), new Person("Mark", "Smith")).collect(Collectors.toList()));
		this.mockMvc.perform(get("/person/smith")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].lastName").value("Smith"))
				.andExpect(jsonPath("$[1].lastName").value("Smith"));
	}

	@Test
	public void shouldRegisterPerson() throws Exception {
		Integer statusCode = 201;
		Person p = new Person("Abhijit", "Smith");
		when(personDataService.register(p)).thenReturn(HttpStatus.CREATED);
		this.mockMvc
				.perform(
						post("/person").contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(p)))
				.andDo(print()).andExpect(status().isCreated());
	}

	@Test
	public void shouldUpdatePerson() throws Exception {
		Person p = new Person("Abhijit", "Archer");
		String newName = "NewName";
		when(personDataService.udatePerson(newName, p)).thenReturn(HttpStatus.NO_CONTENT);
		this.mockMvc
				.perform(put("/person/NewName").contentType(APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(p)))
				.andDo(print()).andExpect(status().isNoContent());
	}

	@Test
	public void shouldReturnBadRequest() throws Exception {
		Person p = new Person("", "");
		String newName = "NewName";
		// when(personDataService.udatePerson(newName,
		// p)).thenReturn(HttpStatus.NO_CONTENT);
		this.mockMvc
				.perform(put("/person/NewName").contentType(APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(p)))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void shouldReturnNotAcceptable() throws Exception {
		Person p = new Person("abhijit", "Dutta");
		String newName = "NewName";
		when(personDataService.udatePerson(any(), any())).thenReturn(HttpStatus.NOT_ACCEPTABLE);
		this.mockMvc
				.perform(put("/person/NewName").contentType(APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(p)))
				.andDo(print()).andExpect(status().isNotAcceptable());
	}
	
	/* mokito dont support this functionalities
	@Test
	public void justTestingAFinalMethod() throws Exception {
		when(myLIst.finalMethod()).thenReturn(1);
		
		assertEquals(1, myLIst.finalMethod());
	}*/
}