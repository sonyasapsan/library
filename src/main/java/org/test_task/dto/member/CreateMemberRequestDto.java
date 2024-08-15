package org.test_task.dto.member;

import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequestDto(@NotBlank
                                     String name) {
}
