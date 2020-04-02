package com.kiran.repository.person;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TestTransaction;

import com.kiran.domain.Address;
import com.kiran.domain.Person;

/*
 * here i am writing each test case as nested class for separate
 * initialization for each test method and committed the data to database 
 * for getting data from db.
 * and removed data in aftereach from db for clean database.
 * 
 * 
 * Most generally data will not be committed when not committed may be we will be 
 * getting data from hibernate cache. so we can alternatively flush and clear cache,
 * to make sure we are testing against DB data.
 * 
 *  @Datajpatest annotated classes are by default all methods are transactional
 *  and rolls back automatically
 *  
 *  @beforeeach and @aftereach gets executed under same transaction started for  @test method
 *  
 *  @Transactional(propagation = Propagation.NOT_SUPPORTED) annotating at class level or
 *  method level makes the @test to run without transaction
 *  
 *  TestTransaction object is used to execute methods of transaction started
 *  
 *  datajpatest always uses by default h2 database with it default config.
 *  We can override h2 config using application.properties
 *  
 *  if we want to change DB to mysql 
 *  we have to  use @AutoConfigureTestDatabase(replace = Replace.NONE)
 *  annotation. Then provide application.properties config.
 *  
 */
@ExtendWith(SpringExtension.class)
public class PersonRepoTest {

	@Nested
	@DataJpaTest

	// below 3 annotation is provided because this is module based project else not
	// required
	@ContextConfiguration(classes = { PersonRepo.class })
	@EnableJpaRepositories(basePackages = { "com.kiran.repository" })
	@EntityScan(basePackages = { "com.kiran.domain" })

	// @AutoConfigureTestDatabase used to provide custom database configuration from
	// application.properties file
	// if this annotation not provided always h2 database autoconfiguration
	// properties provided by @datajpatest spring and cant be overriden
	@AutoConfigureTestDatabase(replace = Replace.NONE)
	class FindTest {

		Person actual;

		@Autowired
		EntityManager entityManager;

		@Autowired
		TestEntityManager testEntityManager;

		@Autowired
		PersonRepo personRepo;

		@BeforeEach
		void setup() {
			// arrange

			Person person = new Person();
			person.setName("kir");
			Address address = new Address();
			address.setName("#112 kumvempu");
			address.setPincode(560061);
			address.setPerson(person);
			person.getAddress().add(address);

			// Person actual = testEntityManager.persistFlushFind(person);
			actual = personRepo.save(person);

			// we can use TestTransaction for transaction for ending staring
			TestTransaction.flagForCommit();
			TestTransaction.end();
			TestTransaction.start();
		}

		@Test
		public void findByIdTest() {

			// act
			Person expectedResult = personRepo.findById(actual.getId()).get();

			/*
			 * instead of all this below line we can use
			 * testEntityManager.persistFlushFind(obj)
			 * 
			 * testEntityManager.persistAndFlush(person);
			 * testEntityManager.getEntityManager().getTransaction().commit();
			 * testEntityManager.getEntityManager().clear(); Person expectedResult
			 * =testEntityManager.find(Person.class, person.getId()); Optional<Person>
			 * optionalactual = personRepo.findById(person.getId());
			 * 
			 * // assert
			 * assertThat(optionalactual.get().getId()).isEqualTo(expectedResult.getId());
			 * 
			 */

			// assert
			assertThat(actual.getId()).isEqualTo(expectedResult.getId());

		}

		@AfterEach
		void cleanup() {

			// clean db
			Address address = testEntityManager.find(Address.class, actual.getAddress().get(0).getId());
			testEntityManager.remove(address);
			personRepo.delete(actual);

		}
	}

	@Nested
	@DataJpaTest
	@ContextConfiguration(classes = { PersonRepo.class })
	@EnableJpaRepositories(basePackages = { "com.kiran.repository" })
	@EntityScan(basePackages = { "com.kiran.domain" })
	@AutoConfigureTestDatabase(replace = Replace.NONE)
	class SaveTest {

		Person actualPerson;

		@Autowired
		EntityManager entityManager;

		@Autowired
		TestEntityManager testEntityManager;

		@Autowired
		PersonRepo personRepo;

		@BeforeEach
		void setup() {
			// arrange

			actualPerson = new Person();
			actualPerson.setName("kir");
			Address address = new Address();
			address.setName("#112 kumvempu");
			address.setPincode(560061);
			address.setPerson(actualPerson);
			actualPerson.getAddress().add(address);

			// we can use TestTransaction for transaction for ending staring
			TestTransaction.flagForCommit();
			TestTransaction.end();
			TestTransaction.start();

		}

		@Test
		public void checkSaveTest() {

			// act
			personRepo.save(actualPerson);

			// assert
			entityManager.clear();
			Person expected = personRepo.findById(actualPerson.getId()).get();
			assertThat(actualPerson.getId()).isEqualTo(expected.getId());

		}

		@AfterEach
		void cleanup() {

			// clean db
			Address address = entityManager.find(Address.class, actualPerson.getAddress().get(0).getId());
			entityManager.remove(address);
			personRepo.delete(actualPerson);

		}
	}
}
