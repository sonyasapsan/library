package org.test_task.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.member.CreateMemberRequestDto;
import org.test_task.dto.member.MemberDto;
import org.test_task.exception.DataProcessingException;
import org.test_task.exception.EntityNotFoundException;
import org.test_task.mapper.BookMapper;
import org.test_task.mapper.MemberMapper;
import org.test_task.model.Book;
import org.test_task.model.Member;
import org.test_task.repository.BookRepository;
import org.test_task.repository.MemberRepository;
import org.test_task.service.MemberService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final static String CAN_NOT_DELETE_USER_MESSAGE
            = "Can't delete user. The borrowed books must be returned.";
    private final static String NO_BOOK_WITH_SUCH_INFO = "There is no book with such title and author";
    private final static String MEMBER_NOT_FOUND_MESSAGE = "There is no member with such id. ID: ";
    private final static String BOOK_NOT_FOUND_MESSAGE = "There is no book with such id. ID: ";
    private final static String NOT_AVAILABLE_BOOK_MESSAGE = "The book is not available now.";
    private final static String BOOK_CAN_NOT_BE_BORROWED_MESSAGE = "The user is not allowed to borrow one more book.";
    private final static String USER_DOES_NOT_HAVE_BOOK_MESSAGE = "The user doesn't have this book";
    private final static String PROPERTY_NAME = "book.amount";
    private MemberRepository memberRepository;
    private MemberMapper memberMapper;
    private BookRepository bookRepository;
    private BookMapper bookMapper;
    private Environment environment;

    @Override
    public MemberDto save(CreateMemberRequestDto requestDto) {
        Member member = memberMapper.toMemberModel(requestDto);
        member.setMembershipDate(LocalDate.now());
        member.setAvailableBookAmount(Integer.parseInt(environment.getProperty(PROPERTY_NAME)));
        return memberMapper.toMemberDto(memberRepository.save(member));
    }

    @Override
    public List<MemberDto> getAll(Pageable pageable) {
        return memberRepository.findAll(pageable).stream()
                .map(memberMapper::toMemberDto)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDto findById(Long id) {
        return memberMapper.toMemberDto(getMember(id));
    }

    @Override
    public void deleteById(Long id) {
        if (getMember(id).getBooks().size() != 0) {
            throw new DataProcessingException(CAN_NOT_DELETE_USER_MESSAGE);
        }
        memberRepository.deleteById(id);
    }

    @Override
    public MemberDto updateById(Long id, CreateMemberRequestDto requestDto) {
        if (!memberRepository.existsById(id)) {
            throw new EntityNotFoundException(MEMBER_NOT_FOUND_MESSAGE + id);
        }
        Member member = memberMapper.toMemberModel(requestDto);
        member.setId(id);
        return memberMapper.toMemberDto(memberRepository.save(member));
    }

    @Override
    public MemberDto borrowBook(CreateBookRequestDto requestDto, Long id) {
        Member member = getMember(id);
        if (member.getAvailableBookAmount() == 0) {
            throw new DataProcessingException(BOOK_CAN_NOT_BE_BORROWED_MESSAGE);
        }
        Book book = getBook(requestDto);
        if(book.getAmount() == 0) {
            throw new DataProcessingException(NOT_AVAILABLE_BOOK_MESSAGE);
        }
        List<Book> books = member.getBooks();
        books.add(book);
        book.setAmount(book.getAmount() - 1);
        bookRepository.save(book);
        member.setBooks(books);
        member.setAvailableBookAmount(member.getAvailableBookAmount() - 1);
        return memberMapper.toMemberDto(memberRepository.save(member));
    }

    @Override
    public MemberDto returnBook(CreateBookRequestDto requestDto, Long id) {
        Member member = getMember(id);
        List<Book> books = member.getBooks();
        Book book = getBook(requestDto);
        if (!books.remove(book)) {
            throw new EntityNotFoundException(USER_DOES_NOT_HAVE_BOOK_MESSAGE);
        }
        book.setAmount(book.getAmount() + 1);
        bookRepository.save(book);
        member.setBooks(books);
        member.setAvailableBookAmount(member.getAvailableBookAmount() + 1);
        return memberMapper.toMemberDto(memberRepository.save(member));
    }

    @Override
    public List<BookDto> getAllBooksByMemberName(Pageable pageable, String name) {
        return memberRepository.findAllBooksByMemberName(name, pageable)
                .getContent().stream()
                .map(bookMapper::toBookDto)
                .collect(Collectors.toList());
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(MEMBER_NOT_FOUND_MESSAGE)
        );
    }

    private Book getBook(CreateBookRequestDto requestDto) {
        return bookRepository.findByAuthorAndTitle(requestDto.title(), requestDto.author())
                .orElseThrow(() -> new EntityNotFoundException(NO_BOOK_WITH_SUCH_INFO));
    }
}
