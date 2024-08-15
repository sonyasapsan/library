package org.test_task.dto.book;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ShowBookInfoResponseDto {
    private String title;
    private Integer amount;
}
