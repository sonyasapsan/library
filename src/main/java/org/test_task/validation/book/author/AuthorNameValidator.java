package org.test_task.validation.book.author;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.test_task.validation.book.title.Title;

import java.util.regex.Pattern;

public class AuthorNameValidator implements ConstraintValidator<AuthorName, String> {
    private static final Pattern PATTERN_OF_NAME = Pattern.compile("^[A-Z][a-z]* [A-Z][a-z]*$");

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return name != null && PATTERN_OF_NAME.matcher(name).matches();
    }
}
