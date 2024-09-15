package com.ratmir.springcourse.repositories;

import com.ratmir.springcourse.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    //Поиск подстроки 'keyword' в названии книги
    @Query("select b from Book b where upper(b.title) like upper(concat('%', :keyword, '%'))")
    List<Book> findByTitleContainingWord(String keyword);
}
