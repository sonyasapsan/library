package org.test_task.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.book.ShowBookInfoResponseDto;
import org.test_task.exception.DataProcessingException;
import org.test_task.exception.EntityNotFoundException;
import org.test_task.mapper.BookMapper;
import org.test_task.model.Book;
import org.test_task.repository.BookRepository;
import org.test_task.service.BookService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final static String BOOK_NOT_FOUND_MESSAGE = "There is no book with such id. ID: ";
    private final static String BOOK_CAN_NOT_BE_DELETED_MESSAGE
            = "Can't delete this book, because some of them are borrowed.";
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        int amount = bookRepository.getBookAmountByTitleAndAuthor(requestDto.title(),
                requestDto.author()).orElse(0);
        Book book = bookMapper.toBookModel(requestDto);
        if (amount != 0) {
            Long id = bookRepository.getIdByTitleAndAuthor(requestDto.title(), requestDto.author());
            book.setId(id);
        }
        book.setAmount(++amount);
        return bookMapper.toBookDto(bookRepository.save(book));
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapper.toBookDto(bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(BOOK_NOT_FOUND_MESSAGE + id)
        ));
    }

    @Override
    public void deleteById(Long id) {
        if (bookRepository.areBookBorrowed(id)) {
            throw new DataProcessingException(BOOK_CAN_NOT_BE_DELETED_MESSAGE);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException(BOOK_NOT_FOUND_MESSAGE + id);
        }
        Book book = bookMapper.toBookModel(requestDto);
        book.setId(id);
        return bookMapper.toBookDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toBookDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findDistinctBorrowedBookTitle(Pageable pageable) {
        return bookRepository.findDistinctBorrowedBookTitle(pageable).getContent();
    }

    @Override
    public List<ShowBookInfoResponseDto> getDistinctBookTitlesAndAmount(Pageable pageable) {
        List<Book> books = bookRepository.findAllBorrowedBooks(pageable).getContent();
        Map<String, Long> bookInfo = books.stream()
                .collect(Collectors.groupingBy(Book::getTitle, Collectors.counting()));
        List<ShowBookInfoResponseDto> showBooksInfoList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : bookInfo.entrySet()) {
            showBooksInfoList.add(new ShowBookInfoResponseDto(entry.getKey(), entry.getValue().intValue()));
        }
        return showBooksInfoList;
    }
}
