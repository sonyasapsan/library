package org.test_task.service;

import org.springframework.data.domain.Pageable;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.book.ShowBookInfoResponseDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    List<BookDto> getAll(Pageable pageable);

    List<String> findDistinctBorrowedBookTitle(Pageable pageable);

    List<ShowBookInfoResponseDto> getDistinctBookTitlesAndAmount(Pageable pageable);
}
