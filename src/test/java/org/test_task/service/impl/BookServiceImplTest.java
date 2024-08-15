package org.test_task.service.impl;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.book.ShowBookInfoResponseDto;
import org.test_task.exception.DataProcessingException;
import org.test_task.exception.EntityNotFoundException;
import org.test_task.mapper.BookMapper;
import org.test_task.model.Book;
import org.test_task.repository.BookRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Spy
    private BookMapper bookMapper;

    @Test
    @DisplayName("Save first book, valid case")
    public void saveFirstBook_validCase_returnBookDto() {
        Book book = getBook();
        CreateBookRequestDto request = getCreateBookRequestDto();
        when(bookRepository.getBookAmountByTitleAndAuthor(request.title(), request.author()))
                .thenReturn(Optional.of(0));
        when(bookMapper.toBookModel(request)).thenReturn(book);
        book.setAmount(book.getAmount() + 1);
        when(bookRepository.save(book)).thenReturn(book);
        BookDto expected = new BookDto("A book", "An author", 1);
        when(bookMapper.toBookDto(book)).thenReturn(expected);
        BookDto actual = bookService.save(request);
        assertEquals(expected, actual);
        assertEquals(1, actual.amount());
    }

    @Test
    @DisplayName("Save second book, valid case")
    public void saveSecondBook_validCase_returnBookDto() {
        Book book = getBook();
        book.setAmount(1);
        CreateBookRequestDto request = getCreateBookRequestDto();
        when(bookRepository.getBookAmountByTitleAndAuthor(request.title(), request.author()))
                .thenReturn(Optional.of(1));
        when(bookMapper.toBookModel(request)).thenReturn(book);
        book.setAmount(book.getAmount() + 1);
        when(bookRepository.save(book)).thenReturn(book);
        BookDto expected = new BookDto("A book", "An author", 2);
        when(bookMapper.toBookDto(book)).thenReturn(expected);
        BookDto actual = bookService.save(request);
        assertEquals(expected, actual);
        assertEquals(2, actual.amount());
    }

    @Test
    @DisplayName("Find book by id, valid case")
    public void findBookById_validCase_returnBookDto() {
        Book book = getBook();
        BookDto expected = getBookDto();
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toBookDto(book)).thenReturn(expected);
        BookDto actual = bookService.findById(1L);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find book by id, invalid case because of incorrect id")
    public void findBookById_invalidCase_throwException() {
        Long bookId = -1L;
        Mockito.when(bookRepository.findById(bookId)).thenThrow(
                new EntityNotFoundException("There is no book with such id. ID: " + bookId)
        );
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findById(bookId)
        );
        String expected = "There is no book with such id. ID: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete a book, valid case")
    public void deleteById_validCase_deleted() {
        Long bookId = 1L;
        bookService.deleteById(bookId);
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("Delete a book, invalid case because the book is borrowed")
    public void deleteById_invalidCase_throwException() {
        Mockito.when(bookRepository.areBookBorrowed(1L)).thenReturn(true);
        Exception exception = Assertions.assertThrows((DataProcessingException.class),
                () -> bookService.deleteById(1L));
        String expected = "Can't delete this book, because some of them are borrowed.";
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update book by id, valid case")
    public void updateBookById_validCase_returnBookDto() {
        CreateBookRequestDto requestDto = getCreateBookRequestDto();
        Book book = getBook();
        Mockito.when(bookRepository.existsById(1L)).thenReturn(true);
        Mockito.when(bookMapper.toBookModel(requestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toBookDto(book)).thenReturn(getBookDto());
        BookDto expected = getBookDto();
        BookDto actual = bookService.updateById(1L, requestDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update book by id, invalid case because book is not found")
    public void updateBookById_invalidCase_throwException() {
        Long id = -1L;
        Mockito.when(bookRepository.existsById(id)).thenThrow(
                new EntityNotFoundException("There is no book with such id. ID: " + id)
        );
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateById(-1L, getCreateBookRequestDto())
        );
        String expected = "There is no book with such id. ID: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all books, valid case")
    public void getAllBooks_validCase_returnListBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = getBookList();
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        List<BookDto> expected = getBookDtoList();
        for (int i = 0; i < books.size(); i++) {
            Mockito.when(bookMapper.toBookDto(books.get(i))).thenReturn(expected.get(i));
        }
        List<BookDto> actual = bookService.getAll(pageable);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find distinct borrowed books' titles, valid case")
    public void findDistinctBorrowedBookTitle_validCase_returnListString() {
        Pageable pageable = PageRequest.of(0, 10);
        List<String> expected = List.of("A book", "A book 2", "A book 3");
        Page<String> titlePage = new PageImpl<>(expected, pageable, expected.size());
        Mockito.when(bookRepository.findDistinctBorrowedBookTitle(pageable)).thenReturn(titlePage);
        List<String> actual = bookService.findDistinctBorrowedBookTitle(pageable);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get distinct book's info, such as title and amount, valid case")
    public void getDistinctBookTitlesAndAmount_validCase_returnShowBookInfoResponseDtoList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = getBookList();
        Book book = getBook();
        book.setAmount(3);
        books.add(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        Mockito.when(bookRepository.findAllBorrowedBooks(pageable)).thenReturn(bookPage);
        List<ShowBookInfoResponseDto> expected = List.of(
                new ShowBookInfoResponseDto("A book", 2),
                new ShowBookInfoResponseDto("A book 2", 1),
                new ShowBookInfoResponseDto("A book 3", 1)
        );
        List<ShowBookInfoResponseDto> actual = bookService.getDistinctBookTitlesAndAmount(pageable);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    private static BookDto getBookDto() {
        return new BookDto("A book", "An author", 1);
    }

    private static CreateBookRequestDto getCreateBookRequestDto() {
        return new CreateBookRequestDto("A book", "An author");
    }

    private static Book getBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("A book");
        book.setAuthor("An author");
        book.setAmount(0);
        return book;
    }

    private static List<Book> getBookList() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("A book");
        book.setAuthor("An author");
        book.setAmount(3);
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("A book 2");
        book2.setAuthor("An author 2");
        book2.setAmount(1);
        Book book3 = new Book();
        book3.setId(3L);
        book3.setTitle("A book 3");
        book3.setAuthor("An author 3");
        book3.setAmount(5);
        List<Book> books = new ArrayList<>(List.of(book, book2, book3));
        return books;
    }

    private static List<BookDto> getBookDtoList() {
        BookDto bookDto = new BookDto("A book", "An author", 3);
        BookDto bookDto2 = new BookDto("A book 2", "An author 2", 1);
        BookDto bookDto3 = new BookDto("A book 3", "An author 3", 5);
        return List.of(bookDto, bookDto2, bookDto3);
    }
}