package uk.co.huntersix.spring.rest.controller;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;

import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest extends HttpMockMvcTest{
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnPersonDetails() throws Exception {
        assertThat(
            this.restTemplate.getForObject(
            		getHostNameAndPort() + "/person/smith/mary",
                String.class
            )
        ).contains("Mary");
    }
    
   
    @Test
    public void shouldReturnNotFoundStatusCodeWhenSendingWrongFirstName() throws Exception {
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(getHostNameAndPort() + "/person/smith/abhijit", 
    			String.class);
   
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
       
    }
    
    
    @Test
    public void shouldReturnNotFoundStatusCodeWhenSendingWrongLastName() throws Exception {
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(getHostNameAndPort() + "/person/dutta/mary", 
    			String.class);
   
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
       
    }
    
      
    @Test
    public void shouldReturnFoundStatusCodeWhenSendingCorrectEntry() throws Exception {
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(getHostNameAndPort() + "/person/smith/mary", 
    			String.class);
    	
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    
    @Test
    public void shouldReturnListOfPersonDetails() throws Exception {
    	
    	ResponseEntity<List<Person>> response =
    	        restTemplate.exchange(getHostNameAndPort() + "/person/smith",
    	                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Person>>() {
    	            });
    	
    	List<Person> pList = response.getBody();
    	
    	for(Person p : pList) {
    		assertThat(p.getLastName().equals("Smith"));
    	}
       
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    
  
    @Test
    public void shouldReturnCreatedResponseForUniqueValue() throws Exception {
    	
    	Person person = new Person("Abhijit","Dutta");
    	
    	ResponseEntity<String> response = restTemplate.postForEntity(getHostNameAndPort() + "/person", person, String.class);
       
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    	
    }
    
    
    
    @Test
    public void shouldReturnConflictResponseForDuplicateEntry() throws Exception {
    	// Below entry already available in the Person list - hence duplicate entry
    	Person person = new Person("Mary", "Smith");
    	
    	ResponseEntity<String> response = restTemplate.postForEntity(getHostNameAndPort() + "/person", person, String.class);
        
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    	
    }
    
    
    @Test
    public void shouldReturnBadRequestResponseForEmptyEntry() throws Exception {
    	// Below entry already available in the Person list - hence duplicate entry
    	Person person = new Person("", "");
    	
    	ResponseEntity<String> response = restTemplate.postForEntity(getHostNameAndPort() + "/person", person, String.class);
        
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    	
    }
      
    
    
    @Test
    public void shouldReturnNoConflictStatusCodeForAnExistingRecords() throws Exception {
    	
    	JSONObject loginJSonRequest = new JSONObject();
    	loginJSonRequest.put("firstName", "Collin");
    	loginJSonRequest.put("lastName", "Brown");
    	
    	RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(getHostNameAndPort() + "/person/Abhijit")
				.accept(MediaType.APPLICATION_JSON)
				.content(loginJSonRequest.toString())
				.contentType(MediaType.APPLICATION_JSON);
    	
    	MvcResult result = getMockMvc().perform(requestBuilder).andReturn();

    	assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());	
    	
    	
    }
    
    
    @Test
    public void shouldReturnNotAcceptableStatusCodeForAnNonExistingRecords() throws Exception {
    	
    	JSONObject loginJSonRequest = new JSONObject();
    	loginJSonRequest.put("firstName", "Mary");
    	loginJSonRequest.put("lastName", "Kom");
    	
    	RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(getHostNameAndPort() + "/person/Abhijit")
				.accept(MediaType.APPLICATION_JSON)
				.content(loginJSonRequest.toString())
				.contentType(MediaType.APPLICATION_JSON);
    	
    	MvcResult result = getMockMvc().perform(requestBuilder).andReturn();

    	assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());	
    	
    }
    
    
    @Test
    public void shouldReturnBadRequestStatusCodeForAnEmptyRecords() throws Exception {
    	
    	JSONObject loginJSonRequest = new JSONObject();
    	loginJSonRequest.put("firstName", "");
    	loginJSonRequest.put("lastName", "");
    	
    	RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(getHostNameAndPort() + "/person/Abhijit")
				.accept(MediaType.APPLICATION_JSON)
				.content(loginJSonRequest.toString())
				.contentType(MediaType.APPLICATION_JSON);
    	
    	MvcResult result = getMockMvc().perform(requestBuilder).andReturn();

    	assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());	
    	
    }
    
    public String getHostNameAndPort() {
    	return "http://localhost:" + port;
    }
}