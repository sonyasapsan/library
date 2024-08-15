package org.test_task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test_task.dto.book.BookDto;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.member.CreateMemberRequestDto;
import org.test_task.dto.member.MemberDto;
import org.test_task.service.MemberService;

import java.util.List;

@Tag(name = "Member management", description = "Endpoint for managing members")
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add new member to system", description = "Method save a member to system")
    public MemberDto create(@RequestBody @Valid CreateMemberRequestDto requestDto) {
        return memberService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get all members", description = "Method allows to retrieve list of members")
    public List<MemberDto> getAll(Pageable pageable) {
        return memberService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find member by id", description = "You can find here a member by unique identifier")
    public MemberDto findById(@PathVariable @Positive Long id) {
        return memberService.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete member by id", description = "Here you can delete member by id."
            + " The member must return all borrowed book firstly")
    public void delete(@PathVariable @Positive Long id) {
        memberService.deleteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update member by id", description = "The method allows to rewrite some"
            + " information about the member")
    public MemberDto update(@RequestBody @Valid CreateMemberRequestDto requestDto,
                            @PathVariable @Positive Long id) {
        return memberService.updateById(id, requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/borrow")
    @Operation(summary = "Borrow a book", description = "Here member can borrow a book"
            + " by it's title and author's name")
    public MemberDto borrowBook(@RequestBody @Validated CreateBookRequestDto requestDto,
                           @PathVariable @Positive Long id) {
        return memberService.borrowBook(requestDto, id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/return")
    @Operation(summary = "Return a book", description = "Here member can return a book"
            + " by it's title and author's name")
    public MemberDto returnBook(@RequestBody @Validated CreateBookRequestDto requestDto,
                           @PathVariable @Positive Long id) {
        return memberService.returnBook(requestDto, id);
    }

    @GetMapping("/{name}/books")
    @Operation(summary = "Get all borrowed books by member's name", description = "This method return all "
            + "borrowed books by member's name")
    public List<BookDto> getAllBooksByMemberName(Pageable pageable, @PathVariable @NotBlank String name) {
        return memberService.getAllBooksByMemberName(pageable, name);
    }
}
