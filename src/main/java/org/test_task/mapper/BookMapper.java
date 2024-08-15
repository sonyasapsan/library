package org.test_task.mapper;

import org.mapstruct.Mapper;
import org.test_task.config.MapperConfig;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toBookDto(Book book);

    Book toBookModel(CreateBookRequestDto requestDto);
}
