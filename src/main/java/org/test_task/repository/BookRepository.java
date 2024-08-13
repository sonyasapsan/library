package org.test_task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test_task.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
