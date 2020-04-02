package com.kiran.service.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kiran.domain.Person;
import com.kiran.repository.person.PersonRepo;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

	@Mock
	PersonRepo personRepo;

	@InjectMocks
	PersonServiceImpl personService;

	@Test
	void getPersonTest() {

		// arrange
		Person person = new Person();
		person.setId(1);
		person.setName("kir");

		// stub
		doReturn(Optional.of(person)).when(personRepo).findById(1);

		// act
		Person resultPerson = personService.getPerson(1);

		// assert
		assertThat(person).isEqualTo(resultPerson);

	}

	@Test
	void getPersonNotFoundException() {

		// arrange
		doReturn(Optional.ofNullable(null)).when(personRepo).findById(anyInt());

		// assert
		assertThrows(CustomPersonException.NotFound.class, () -> personService.getPerson(1));
		verify(personRepo).findById(anyInt());
	}

	@Test
	void savePersonTest() {
		// arrange
		Person person = new Person();
		person.setId(1);
		person.setName("kir");

		// stub
		doReturn(person).when(personRepo).save(any(Person.class));

		// act
		personService.save(person);

		// assert
		verify(personRepo).save(any(Person.class));

	}
}
