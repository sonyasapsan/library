package org.test_task.dto.member;

import java.time.LocalDate;

public record MemberDto(String name,
                        LocalDate membershipDate,
                        int availableBookAmount) {
}
