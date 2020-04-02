package com.kiran.controller.person;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kiran.domain.Person;
import com.kiran.service.person.PersonService;

@RequestMapping("/test")
@org.springframework.web.bind.annotation.RestController
public class PersonController {

	@Autowired
	PersonService personService;

	@GetMapping("/check/{id}")
	Person getPerson(@PathVariable("id") Integer id) {
		return personService.getPerson(id);
	}

	@PostMapping("/saveperson")
	public ResponseEntity savePerson(@RequestBody Person person) throws URISyntaxException {
		personService.save(person);
		return ResponseEntity.created(new URI("/check/")).build();
	}
}
