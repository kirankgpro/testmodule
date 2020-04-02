package com.kiran.repository.person;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kiran.domain.Person;

public interface PersonRepo extends JpaRepository<Person, Integer> {

}
