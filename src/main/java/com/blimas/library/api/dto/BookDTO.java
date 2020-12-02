package com.blimas.library.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String author;

    @NotEmpty
    private String isbn;
}
