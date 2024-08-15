package org.test_task.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private MemberRepository memberRepository;
    @Spy
    private MemberMapper memberMapper;
    @Spy
    private Environment environment;
    @Spy
    private BookMapper bookMapper;

    @Test
    @DisplayName("Save a member, valid case")
    public void saveMember_validCase_returnMemberDto() {
        Member member = getMember();
        MemberDto expected = getMemberDto();
        CreateMemberRequestDto requestDto = getCreateMemberRequestDto();
        Mockito.when(memberMapper.toMemberModel(requestDto)).thenReturn(member);
        Mockito.when(environment.getProperty("book.amount")).thenReturn("10");
        Mockito.when(memberRepository.save(member)).thenReturn(member);
        Mockito.when(memberMapper.toMemberDto(member)).thenReturn(expected);
        MemberDto actual = memberService.save(requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find member by id, valid case")
    public void findMemberById_validCase_returnMemberDto(){
        Member member = getMember();
        MemberDto expected = getMemberDto();
        Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        Mockito.when(memberMapper.toMemberDto(member)).thenReturn(expected);
        MemberDto actual = memberService.findById(1L);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find member by id, invalid case because of incorrect id")
    public void findMemberById_invalidCase_throwException() {
        Long id = -1L;
        Mockito.when(memberRepository.findById(id)).thenThrow(
                new EntityNotFoundException("There is no member with such id. ID: " + id)
        );
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> memberService.findById(id)
        );
        String expected = "There is no member with such id. ID: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete a member, valid case")
    public void deleteById_validCase_deleted() {
        Long id = 1L;
        Mockito.when(memberRepository.findById(id)).thenReturn(Optional.of(getMember()));
        memberService.deleteById(id);
        Mockito.verify(memberRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Delete a member, invalid case because the member must return books firstly")
    public void deleteById_invalidCase_throwException() {
        Long id = 2L;
        Mockito.when(memberRepository.findById(id)).thenReturn(Optional.of(getMemberWithBooks()));
        Exception exception = Assertions.assertThrows((DataProcessingException.class),
                () -> memberService.deleteById(id));
        String expected = "Can't delete user. The borrowed books must be returned.";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update member by id, valid case")
    public void updateMemberById_validCase_returnMember() {
        CreateMemberRequestDto requestDto = getCreateMemberRequestDto();
        Member member = getMember();
        MemberDto expected = getMemberDto();
        Mockito.when(memberRepository.existsById(1L)).thenReturn(true);
        Mockito.when(memberMapper.toMemberModel(requestDto)).thenReturn(member);
        Mockito.when(memberRepository.save(member)).thenReturn(member);
        Mockito.when(memberMapper.toMemberDto(member)).thenReturn(expected);
        MemberDto actual = memberService.updateById(1L, requestDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update member by id, invalid case because member is not found")
    public void updateMemberById_invalidCase_throwException() {
        Long id = -1L;
        Mockito.when(memberRepository.existsById(id)).thenThrow(
                new EntityNotFoundException("There is no member with such id. ID: " + id)
        );
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> memberService.updateById(-1L, getCreateMemberRequestDto())
        );
        String expected = "There is no member with such id. ID: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all members, valid case")
    public void getAllMembers_validCase_returnListMemberDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Member> members = getMembersList();
        Page<Member> memberPage = new PageImpl<>(members, pageable, members.size());
        Mockito.when(memberRepository.findAll(pageable)).thenReturn(memberPage);
        List<MemberDto> expected = getMemberDtoList();
        for (int i = 0; i < members.size(); i++) {
            Mockito.when(memberMapper.toMemberDto(members.get(i))).thenReturn(expected.get(i));
        }
        List<MemberDto> actual = memberService.getAll(pageable);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Member borrows a book, valid case")
    public void borrowBook_validCase_returnMemberDto() {
        Member member = getMember();
        Book book = getBook();
        Mockito.when(bookRepository.findByAuthorAndTitle(book.getTitle(), book.getAuthor()))
                        .thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        book.setAmount(book.getAmount() - 1);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        List<Book> books = new ArrayList<>();
        books.add(book);
        member.setBooks(books);
        member.setAvailableBookAmount(member.getAvailableBookAmount() - 1);
        Mockito.when(memberRepository.save(member)).thenReturn(member);
        MemberDto expected = new MemberDto("Name", LocalDate.now(), 9);
        Mockito.when(memberMapper.toMemberDto(member)).thenReturn(expected);
        MemberDto actual = memberService.borrowBook(getCreateBookRequestDto(), 1L);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return book, valid case")
    public void returnBook_validCase_returnMemberDto() {
        Member member = getMember();
        Book book = getBook();
        book.setAmount(2);
        member.getBooks().add(book);
        Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        Mockito.when(bookRepository.findByAuthorAndTitle(Mockito.eq(book.getTitle()),
                        Mockito.eq(book.getAuthor()))).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);
        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(member);
        MemberDto expected = getMemberDto();
        Mockito.when(memberMapper.toMemberDto(Mockito.any(Member.class))).thenReturn(expected);
        MemberDto actual = memberService.returnBook(getCreateBookRequestDto(), 1L);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all books by member name, valid case")
    public void getAllBooksByMemberName_validCase_returnListBookDto() {
        Member member = getMemberWithBooks();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = member.getBooks();
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        Mockito.when(memberRepository.findAllBooksByMemberName(member.getName(), pageable))
                .thenReturn(bookPage);
        BookDto book = new BookDto("A book", "An author", 3);
        Mockito.when(bookMapper.toBookDto(books.get(0))).thenReturn(book);
        List<BookDto> expected = List.of(book);
        List<BookDto> actual = memberService.getAllBooksByMemberName(pageable, member.getName());
        assertEquals(expected, actual);
    }

    private static List<MemberDto> getMemberDtoList() {
        MemberDto memberDto = getMemberDto();
        MemberDto memberDto2 = new MemberDto("Name 2", LocalDate.now(), 9);
        return List.of(memberDto, memberDto2);
    }

    private static List<Member> getMembersList() {
        Member member = getMember();
        Member member2 = getMemberWithBooks();
        return List.of(member, member2);
    }

    private static CreateMemberRequestDto getCreateMemberRequestDto() {
        return new  CreateMemberRequestDto("Name");
    }

    private static MemberDto getMemberDto() {
        return new MemberDto("Name", LocalDate.now(), 10);
    }

    private static Member getMember() {
        Member member = new Member();
        member.setId(1L);
        member.setName("Name");
        member.setMembershipDate(LocalDate.now());
        member.setAvailableBookAmount(10);
        member.setBooks(new ArrayList<>());
        return member;
    }

    private static Member getMemberWithBooks() {
        Member member = new Member();
        member.setId(2L);
        member.setName("Name 2");
        member.setMembershipDate(LocalDate.now());
        member.setAvailableBookAmount(9);
        List<Book> books = new ArrayList<>();
        books.add(getBook());
        member.setBooks(books);
        return member;
    }

    private static Book getBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("A book");
        book.setAuthor("An author");
        book.setAmount(3);
        return book;
    }

    private static CreateBookRequestDto getCreateBookRequestDto() {
        return new CreateBookRequestDto("A book", "An author");
    }
}