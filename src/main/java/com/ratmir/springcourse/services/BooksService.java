package com.ratmir.springcourse.services;

import com.ratmir.springcourse.models.Book;
import com.ratmir.springcourse.models.Person;
import com.ratmir.springcourse.repositories.BooksRepository;
import com.ratmir.springcourse.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;
    private final PeopleRepository peopleRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository, PeopleRepository peopleRepository) {
        this.booksRepository = booksRepository;
        this.peopleRepository = peopleRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public Book findById(int id) {
        Optional<Book> foundBook = (booksRepository.findById(id));
        return foundBook.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void release(int bookId) {
        Optional<Book> foundBook = booksRepository.findById(bookId);

        if(foundBook.isPresent()) {
            Book book = foundBook.get();
            Person owner = book.getOwner();

            // Удаляем время взятия
            book.setTakenAt(null);

            // Использую метод removeBook() у человека, и в этом методе у книги вызывается setOwner(null)
            // Т.е. применяю изменения с двух сторон, чтобы в кэше hibernate лежали актуальные данные
            owner.removeBook(book);
        }
    }

    @Transactional
    public void assign(int personId, int bookId) {
        Optional<Book> foundBook = booksRepository.findById(bookId);
        Optional<Person> foundPerson = peopleRepository.findById(personId);

        if(foundBook.isPresent() && foundPerson.isPresent()) {
            Book book = foundBook.get();
            Person person = foundPerson.get();

            // Устанавливаем время взятия книги
            book.setTakenAt(new Date());

            // Использую метод addBook() у человека, и в этом методе у книги вызывается setOwner()
            // Т.е. устанавливаю связь с двух сторон, чтобы в кэше hibernate были актуальные данные
            person.addBook(book);
        }
    }

    public List<Book> paginate(Integer booksPerPage, Integer page, long booksCount) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public List<Book> sort() {
        return booksRepository.findAll(Sort.by("productionYear"));
    }

    public List<Book> paginateAndSort(Integer booksPerPage, Integer page, long booksCount) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("productionYear"))).getContent();
    }

    public long count() {
        return booksRepository.count();
    }

    public List<Book> findBooksContainingWord(String keyword) {
        return booksRepository.findByTitleContainingWord(keyword);
    }
}
