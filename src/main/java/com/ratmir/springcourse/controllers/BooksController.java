package com.ratmir.springcourse.controllers;

import com.ratmir.springcourse.models.Book;
import com.ratmir.springcourse.models.Person;
import com.ratmir.springcourse.services.BooksService;
import com.ratmir.springcourse.services.PeopleService;
import com.ratmir.springcourse.util.BookValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService peopleService;
    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService, BookValidator bookValidator) {
        this.booksService = booksService;
        this.peopleService = peopleService;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public String showBooks(Model model,
                            @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "sort_by_year", required = false) Boolean sortByYear,
                            @RequestParam(value = "show_all", required = false) Boolean showAll
    ) {
        List<Book> books = new ArrayList<>();
        long booksCount = booksService.count();

        if(booksPerPage != null && page != null) {
            List<Integer> values = correct(booksPerPage, page, booksCount);
            booksPerPage = values.get(0);
            page = values.get(1);
        }

        if(showAll != null && showAll) {
            books = booksService.findAll();
        }
        // Пагинация + Сортировка
        else if(booksPerPage != null && page != null &&
                sortByYear != null && sortByYear) {
            model.addAttribute("isSorted", "true");
            books = booksService.paginateAndSort(booksPerPage, page, booksCount);
        }
        // Пагинация
        else if(booksPerPage != null && page != null) {
            model.addAttribute("isSorted", "false");
            books = booksService.paginate(booksPerPage, page, booksCount);
        }
        // Сортировка
        else if(sortByYear != null && sortByYear) {
            model.addAttribute("isSorted", "true");
            books = booksService.sort();
        }
        // Значения для пагинации по умолчанию(booksPerPage=10, page=0)
        else {
            model.addAttribute("isSorted", "false");
            if(booksCount > 0) {
                books = booksService.paginate((int) Math.min(booksCount, 10), 0, booksCount);
            }
        }

        model.addAttribute("booksPerPage", (booksPerPage == null ? Math.min(booksCount, 10) : booksPerPage));
        model.addAttribute("page", (page == null ? 0 : page));
        model.addAttribute("booksCount", booksCount);

        model.addAttribute("books", books);
        return "books/books-list";
    }

    private List<Integer> correct(Integer booksPerPage, Integer page, long booksCount) {
        if(booksPerPage != null && page != null) {
            if(booksPerPage < 1 || page < 0) {
                booksPerPage = 5;
                page = 0;
            }
            if(booksPerPage > booksCount) {
                booksPerPage = 20;
            }
            List<Integer> list = List.of(5, 10, 15, 20);
            if(!list.contains(booksPerPage)) {
                booksPerPage = 10;
            }
            if(page > Math.ceil((double)booksCount / (double)booksPerPage)) {
                page = 0;
            }
        }
        return List.of(booksPerPage, page);
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") int id,
                          Model model,
                          @ModelAttribute("person") Person person
    ) {
        Book book = booksService.findById(id);
        model.addAttribute("book", book);

        Optional<Person> bookOwner = Optional.ofNullable(book.getOwner());
        if (bookOwner.isPresent()) {
            model.addAttribute("owner", bookOwner.get());
        } else {
            model.addAttribute("people", peopleService.findAll());
        }

        return "books/book";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int bookId) {
        booksService.release(bookId);
        return "redirect:/books/" + bookId;
    }

    @PatchMapping("/{id}/assign")
    public String assign(@ModelAttribute("person") Person person,
                         @PathVariable("id") int bookId) {
        booksService.assign(person.getId(), bookId);
        return "redirect:/books/" + bookId;
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {

        bookValidator.validate(book, bindingResult);

        if(bindingResult.hasErrors()) {
            return "books/new";
        }

        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model,
                       @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {

        bookValidator.validate(book, bindingResult);

        if(bindingResult.hasErrors()) {
            return "books/edit";
        }
        booksService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(value = "keyword", required = false) String keyword) {

        List<Book> foundBooks = new ArrayList<>();
        if(keyword != null && !keyword.isEmpty()) {
            foundBooks = booksService.findBooksContainingWord(keyword);
        }
        model.addAttribute("books", foundBooks);

        return "books/search";
    }
}
