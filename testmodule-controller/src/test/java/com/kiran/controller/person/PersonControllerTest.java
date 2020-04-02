package com.kiran.controller.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiran.domain.Address;
import com.kiran.domain.Person;
import com.kiran.service.person.CustomPersonException;
import com.kiran.service.person.PersonService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PersonController.class)
class PersonControllerTest {

	@MockBean
	PersonService personService;

	@Inject
	MockMvc mockMvc;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
		reset(personService);
	}

	@Nested
	@DisplayName("test person crud")
	class GetPersonTest {

		@Test
		@DisplayName("testing person")
		void testGetPerson() throws Exception {

			// arrange
			Person person = new Person();
			person.setId(1);
			person.setName("kir");
			Address address = new Address();
			address.setName("#112 kumvempu");
			address.setPincode(560061);
			person.getAddress().add(address);

			String expectedResult = objectToString(person);

			RequestBuilder req = MockMvcRequestBuilders.get("/test/check/1");

			// stub
			doReturn(person).when(personService).getPerson(1);

			// act
			String result = mockMvc.perform(req).andReturn().getResponse().getContentAsString();

			// assert
			verify(personService).getPerson(1);
			assertThat(result).isEqualTo(expectedResult);
		}

		@Test
		void personNotFoundExceptionCatchTest() throws Exception {

			// arrange
			Person person = new Person();
			person.setId(1);
			person.setName("kir");
			String expectedResult = objectToString(person);

			doThrow(CustomPersonException.NotFound.class).when(personService).getPerson(anyInt());

			RequestBuilder req = MockMvcRequestBuilders.get("/test/check/2");

			// act
			mockMvc.perform(req).andExpect(status().isNotFound());

		}

		@Test
		@DisplayName("save person with address")
		void savePersonTest() throws Exception {
			// arrange
			Person person = new Person();
			person.setId(1);
			person.setName("kir");
			Address address = new Address();
			address.setName("#112 kumvempu");
			address.setPincode(560061);
			person.getAddress().add(address);

			RequestBuilder req = MockMvcRequestBuilders.post("/test/saveperson")
					.contentType(MediaType.APPLICATION_JSON_VALUE).content(objectToString(person));

			// stub
			doNothing().when(personService).save(person);

			// act
			MvcResult result = mockMvc.perform(req).andReturn();

			// assert
			verify(personService).save(person);
			assertThat(HttpStatus.CREATED.value()).isEqualTo(result.getResponse().getStatus());

		}

	}

	String objectToString(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}

}
