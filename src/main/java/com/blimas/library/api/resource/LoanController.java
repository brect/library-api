package com.blimas.library.api.resource;


import com.blimas.library.api.dto.BookDTO;
import com.blimas.library.api.dto.LoanDTO;
import com.blimas.library.api.dto.LoanFilterDTO;
import com.blimas.library.api.dto.ReturnedLoanDTO;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.entity.Loan;
import com.blimas.library.api.service.BookService;
import com.blimas.library.api.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan loan = Loan.builder()
                .book(book)
                .customer(request.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        loan = loanService.save(loan);

        return loan.getId();
    }


    @PutMapping("id")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO request) {
        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(request.getReturned());
        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> findLoan(LoanFilterDTO loanFilterDTO, Pageable pageable) {
        Page<Loan> result = loanService.find(loanFilterDTO, pageable);

        List<LoanDTO> loanList = result.getContent().stream().map(
                entity -> {

                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;

                }).collect(Collectors.toList());

        return new PageImpl<>(loanList, pageable, result.getTotalElements());
    }


}
