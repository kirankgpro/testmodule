package com.kiran.service.person;

import com.kiran.domain.Person;

public interface PersonService {

	Person getPerson(Integer id) throws CustomPersonException.NotFound;

	void save(Person person);
}
