package uk.co.huntersix.spring.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.ArrayList;
import java.util.List;

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
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(new Person("Mary", "Smith"));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }
    
    @Test
    public void shouldReturnNotFoundStatusWhenPersonNotFound() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(null);
        this.mockMvc.perform(get("/person/smith/abhijit"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
    
   /* 
    @Test
    public void shouldReturnNotFoundStatusWhenFirstNameNotProvided() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(null);
        this.mockMvc.perform(get("/person/dutta"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }*/
    
    
    @Test
    public void shouldReturnListOfPerson() throws Exception {
    	
    	List<Person> personList = new ArrayList<Person>();
    	
    	personList.add(new Person("Collin", "Smith"));
    	personList.add(new Person("Mark", "Smith"));
    	
        when(personDataService.findPersonList(any())).thenReturn(personList);
        this.mockMvc.perform(get("/person/all/smith"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Smith"))
            .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }
    
    
    @Test
    public void shouldRegisterPerson() throws Exception {
     	Person p  = new Person("Abhijit", "Smith");    	
        when(personDataService.findPerson(any(),any())).thenReturn(null);
        this.mockMvc.perform(post("/person/register")
            .contentType(APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(p)))
            .andDo(print())
            .andExpect(status().isCreated());
    }
    
    
    @Test
    public void shouldReturnConflicStatusForDuplicateEntry() throws Exception {
    	Person person = new Person("Collin", "Brown");
        when(personDataService.findPerson(any(), any())).thenReturn( new Person("Collin", "Brown"));        
        this.mockMvc.perform(post("/person/register")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
    
    @Test
    public void shouldUpdatePerson() throws Exception {
     	Person p  = new Person("Abhijit", "Archer");    	
        when(personDataService.findPerson(any(),any())).thenReturn(p);
        this.mockMvc.perform(put("/person/update/NewName")
            .contentType(APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(p)))
            .andDo(print())
            .andExpect(status().isNoContent());
    }
    
}