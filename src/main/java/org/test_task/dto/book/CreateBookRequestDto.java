package org.test_task.dto.book;

import org.test_task.validation.book.author.AuthorName;
import org.test_task.validation.book.title.Title;

public record CreateBookRequestDto(@Title
                                    String title,
                                   @AuthorName
                                   String author){
}
