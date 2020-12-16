package com.blimas.library.api.resource;


import com.blimas.library.api.dto.BookDTO;
import com.blimas.library.api.dto.LoanDTO;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.entity.Loan;
import com.blimas.library.api.service.BookService;
import com.blimas.library.api.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private LoanService loanService;
    private BookService bookService;
    private ModelMapper modelMapper;

    public LoanController(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanDTO request) {
        Book book = bookService.getBookByIsbn(request.getIsbn())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan loan = Loan.builder()
                .book(book)
                .customer(request.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        loan = loanService.save(loan);

        return loan.getId();
    }


}
