package org.test_task.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test_task.model.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT book.id FROM Book book "
    + "WHERE book.title LIKE :title AND book.author LIKE :author")
    long getIdByTitleAndAuthor(String title, String author);

    @Query("SELECT book.amount FROM Book book "
    + "WHERE book.title LIKE :title AND book.author LIKE :author")
    Optional<Integer> getBookAmountByTitleAndAuthor(String title, String author);

    @Query("SELECT book FROM Book book "
    + " WHERE book.title LIKE :title AND book.author LIKE :author")
    Optional<Book> findByAuthorAndTitle(String title, String author);

    @Query(value = "SELECT COUNT(*) > 0 FROM member_books" +
            " WHERE book_id = :id", nativeQuery = true)
    boolean areBookBorrowed(@Param("id") Long id);

    @Query("SELECT DISTINCT book.title FROM Member member JOIN member.books book")
    Page<String> findDistinctBorrowedBookTitle(Pageable pageable);

    @Query(value = "SELECT b.* FROM member_books mb JOIN books b ON mb.book_id = b.id", nativeQuery = true)
    Page<Book> findAllBorrowedBooks(Pageable pageable);
}
