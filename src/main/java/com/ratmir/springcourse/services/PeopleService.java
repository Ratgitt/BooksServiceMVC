package com.ratmir.springcourse.services;

import com.ratmir.springcourse.models.Book;
import com.ratmir.springcourse.models.Person;
import com.ratmir.springcourse.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findById(int id) {
        Optional<Person> foundPerson = (peopleRepository.findById(id));
        return foundPerson.orElse(null);
    }

    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);

        if(foundPerson.isPresent()) {

            // Использую Hibernate.initialize(),
            // потому что в противном случае
            // Java оптимизирует код и не делает запрос к бд,
            // следовательно мы не получаем никакой список
            Hibernate.initialize(foundPerson.get().getBooks());

            return foundPerson.get().getBooks();
        }

        return Collections.emptyList();
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }
}
