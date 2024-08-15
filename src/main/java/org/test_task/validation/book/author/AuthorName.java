package org.test_task.validation.book.author;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.test_task.validation.book.title.TitleValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TitleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorName {
    String message() default "Invalid format author's name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

