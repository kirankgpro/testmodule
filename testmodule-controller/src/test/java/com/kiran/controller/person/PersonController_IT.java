package com.kiran.controller.person;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiran.Application;
import com.kiran.domain.Address;
import com.kiran.domain.Person;
import com.kiran.repository.person.PersonRepo;

@ActiveProfiles("it")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class PersonController_IT {

	@LocalServerPort
	private Integer port;

	@Inject
	TestRestTemplate restTemplate;

	@Autowired
	EntityManager entityManager;

	@Autowired
	PersonRepo personRepo;

	@Test
	@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
	void getPersonTest() throws URISyntaxException, JsonProcessingException {

		// arrange
		Person person = new Person();
		person.setName("jon");

		// inserted to db and commited
		personRepo.save(person);

		String url = "/check/" + person.getId();
		RequestEntity req = RequestEntity.get(new URI(getFullUrl(url))).build();

		// act
		ResponseEntity<Person> result = restTemplate.exchange(req, Person.class);

		// assert
		assertThat(person.getName()).isEqualTo(result.getBody().getName());

		// act
		ResponseEntity<String> resultt = restTemplate.exchange(req, String.class);

		// assert
		assertThat(objectToString(person)).isEqualTo(resultt.getBody());

		// clean
		personRepo.delete(person);
	}

	@Test
	void getPersonExceptionHandleTest() throws URISyntaxException, JsonProcessingException {

		// arrange
		String url = "/check/500";
		RequestEntity req = RequestEntity.get(new URI(getFullUrl(url))).build();

		// act
		ResponseEntity<Person> result = restTemplate.exchange(req, Person.class);

		// assert
		assertThat(HttpStatus.NOT_FOUND).isEqualTo(result.getStatusCode());
	}

	/*
	 * @Sql(scripts = "classpath:CreateSchema.sql", executionPhase =
	 * Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	 * 
	 * @Sql(scripts = "classpath:DeleteSchema.sql", executionPhase =
	 * Sql.ExecutionPhase.AFTER_TEST_METHOD)
	 * 
	 * or
	 * 
	 * @SqlGroup({
	 * 
	 * @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = ""),
	 * 
	 * @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = ""), })
	 */
	@Test
	void saveTest() throws URISyntaxException {
		// arrange
		Person person = new Person();
		person.setName("kira");
		Address address = new Address();
		address.setName("#112 kumvempu");
		address.setPincode(560061);
		address.setPerson(person);
		person.getAddress().add(address);

		String url = "/saveperson";
		RequestEntity req = RequestEntity.post(new URI(getFullUrl(url))).body(person);

		// act
		ResponseEntity<String> result = restTemplate.exchange(req, String.class);

		// assert
		assertThat(HttpStatus.CREATED).isEqualTo(result.getStatusCode());

		// clean
		// cant perform clean operation because we dont get any id
		// so better we should use @sql @sqlgroup

	}

	private String getFullUrl(String url) {
		return "http://localhost:" + port + "/test" + url;
	}

	private String objectToString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);

	}

}
