package com.ratmir.springcourse.util;

import com.ratmir.springcourse.models.Person;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;

@Component
public class PersonValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if(person.getBirthYear() > Calendar.getInstance().get(Calendar.YEAR)) {
            errors.rejectValue("birthYear", "", "Год рождения не должен превышать текущий год");
        }
    }
}
