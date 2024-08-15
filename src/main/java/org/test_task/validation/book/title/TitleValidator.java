package org.test_task.validation.book.title;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import java.util.regex.Pattern;

public class TitleValidator implements ConstraintValidator<Title, String> {
    private static final Pattern PATTERN_OF_TITLE = Pattern.compile("^[A-Z].{2,}$\n");

    @Override
    public boolean isValid(String title, ConstraintValidatorContext constraintValidatorContext) {
        return title != null && PATTERN_OF_TITLE.matcher(title).matches();
    }
}