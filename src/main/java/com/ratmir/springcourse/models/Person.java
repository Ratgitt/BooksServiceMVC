package com.ratmir.springcourse.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 35, message = "Имя должно быть длиной от 2 до 35 символов")
    private String name;

    @Column(name = "surname")
    @NotEmpty(message = "Фамилия не должна быть пустой")
    @Size(min = 2, max = 35, message = "Фамилия должна быть длиной от 2 до 35 символов")
    private String surname;

    @Column(name = "last_name")
    @Size(max = 35, message = "Максимальная длина 35 символов")
    private String lastName;

    // Максимальный год - текущий (PersonValidator)
    @Min(value = 1900, message = "Минимальный год 1900")
    @Column(name = "birth_year")
    private int birthYear;

    @OneToMany(mappedBy = "owner")
    private List<Book> books;

    public Person() {}

    public Person(String name, String surname, String lastName, int birthYear) {
        this.name = name;
        this.surname = surname;
        this.lastName = lastName;
        this.birthYear = birthYear;
    }

    public void addBook(Book book) {
        if(books == null) {
            books = new ArrayList<>();
        }

        // 1. Добавляем книгу в список книг
        // 2. Устанавливаем владельца для книги
        books.add(book);
        book.setOwner(this);
    }

    public void removeBook(Book book) {
        if(books != null) {
            // 1. Удаляем книгу из списка книг
            // 2. У книги удаляем владельца
            books.remove(book);
            book.setOwner(null);
        }
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && birthYear == person.birthYear && Objects.equals(name, person.name) && Objects.equals(surname, person.surname) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, lastName, birthYear);
    }
}
