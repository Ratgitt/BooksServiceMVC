package com.ratmir.springcourse.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "Book")
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    @NotEmpty(message = "Название не должно быть пустым")
    @Size(max = 75, message = "Максимальная длина 75 символов")
    private String title;

    @Column(name = "author")
    @NotEmpty(message = "Отсутствует автор")
    @Size(max = 100, message = "Максимальная длина 100 символов")
    private String author;

    @Column(name = "production_year")
    @Positive(message = "Год выпуска должен быть положительным числом")
    private int productionYear;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner;

    @Column(name = "taken_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenAt;

    @Transient
    private boolean isExpired;

    public Book() {}

    public Book(String title, String author, int productionYear) {
        this.title = title;
        this.author = author;
        this.productionYear = productionYear;
    }

    public boolean isExpired() {
        setExpired();
        return this.isExpired;
    }

    private void setExpired() {
        if(this.takenAt != null) {
            // Проверяем, прошло ли 10 дней с момента взятия
            Date currentDate = new Date();
            long millisecondsSinceTaken = currentDate.getTime() - this.takenAt.getTime();
            long daysSinceTaken = TimeUnit.DAYS.convert(millisecondsSinceTaken, TimeUnit.MILLISECONDS);

            this.isExpired = daysSinceTaken >= 10;
        }
        else {
            this.isExpired = false;
        }
    }

    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && productionYear == book.productionYear && Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, productionYear);
    }
}
