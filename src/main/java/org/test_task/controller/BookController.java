package org.test_task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.book.ShowBookInfoResponseDto;
import org.test_task.service.BookService;

import java.util.List;

@Tag(name = "Book management", description = "Endpoint for managing books")
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {
    private BookService bookService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add new book", description = "Here you can add a new book. "
            + "If book already exist in system, its' amount would increase by one")
    public BookDto create(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find book by id", description = "You can find here a specific book by unique identifier")
    public BookDto findById(@PathVariable @Positive Long id) {
        return bookService.findById(id);
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "This method returns all books")
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by id", description = "Here you can delete book by id."
            + " The book mustn't be borrowed")
    public void delete(@PathVariable @Positive Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book by id", description = "The method allows to rewrite some"
            + " information about the book")
    public BookDto update(@RequestBody @Valid CreateBookRequestDto requestDto,
                          @PathVariable @Positive Long id){
        return bookService.updateById(id, requestDto);
    }

    @GetMapping("/borrowed-books-titles")
    @Operation(summary = "Retrieve all titles of books which have been borrowed",
    description = "The list of books doesn't have duplicates")
    public List<String> getDistinctBookTitles(Pageable pageable) {
        return bookService.findDistinctBorrowedBookTitle(pageable);
    }

    @GetMapping("/borrowed-books-info")
    @Operation(summary = "Retrieve information books which have been borrowed",
    description = "Method returns titles and amount of books which have been borrowed")
    public List<ShowBookInfoResponseDto> getDistinctBookTitlesAndAmount(Pageable pageable) {
        return bookService.getDistinctBookTitlesAndAmount(pageable);
    }
}
