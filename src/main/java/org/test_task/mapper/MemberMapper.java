package org.test_task.mapper;

import org.mapstruct.Mapper;
import org.test_task.config.MapperConfig;
import org.test_task.dto.book.CreateBookRequestDto;
import org.test_task.dto.member.CreateMemberRequestDto;
import org.test_task.dto.member.MemberDto;
import org.test_task.model.Member;

@Mapper(config = MapperConfig.class)
public interface MemberMapper {
    MemberDto toMemberDto(Member member);

    Member toMemberModel(CreateMemberRequestDto memberDto);
}
