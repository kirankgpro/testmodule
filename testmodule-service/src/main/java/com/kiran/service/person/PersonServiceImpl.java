package com.kiran.service.person;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiran.domain.Address;
import com.kiran.domain.Person;
import com.kiran.repository.person.PersonRepo;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

	@Autowired
	PersonRepo personRepo;

	@Override
	public Person getPerson(Integer id) throws CustomPersonException.NotFound {

		Optional<Person> optional = personRepo.findById(id);

		return optional.orElseThrow(() -> new CustomPersonException.NotFound("not found"));
	}

	@Override
	public void save(Person person) {
		for (Address address : person.getAddress()) {
			address.setPerson(person);
		}
		personRepo.save(person);
	}

}
