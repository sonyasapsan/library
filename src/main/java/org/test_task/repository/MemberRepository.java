package org.test_task.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test_task.model.Book;
import org.test_task.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT books FROM Member member JOIN member.books books WHERE member.name = :name")
    Page<Book> findAllBooksByMemberName(@Param("name") String name, Pageable pageable);
}
