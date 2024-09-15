package com.ratmir.springcourse.util;

import com.ratmir.springcourse.models.Book;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;

@Component
public class BookValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        if(book.getProductionYear() > Calendar.getInstance().get(Calendar.YEAR)) {
            errors.rejectValue("productionYear", "", "Год выпуска не должен превышать текущий год");
        }
    }
}
