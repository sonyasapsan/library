package org.test_task.service;

import org.springframework.data.domain.Pageable;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.member.CreateMemberRequestDto;
import org.test_task.dto.member.MemberDto;

import java.util.List;

public interface MemberService {
    MemberDto save(CreateMemberRequestDto requestDto);

    List<MemberDto> getAll(Pageable pageable);

    MemberDto findById(Long id);

    void deleteById(Long id);

    MemberDto updateById(Long id, CreateMemberRequestDto requestDto);

    MemberDto borrowBook(CreateBookRequestDto requestDto, Long id);

    MemberDto returnBook(CreateBookRequestDto requestDto, Long id);

    List<BookDto> getAllBooksByMemberName(Pageable pageable, String name);
}
