package com.blimas.library.service;

import com.blimas.library.api.dto.LoanFilterDTO;
import com.blimas.library.api.exception.BussinessException;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.entity.Loan;
import com.blimas.library.api.model.repository.BookRepository;
import com.blimas.library.api.model.repository.LoanRepository;
import com.blimas.library.api.service.BookService;
import com.blimas.library.api.service.LoanService;
import com.blimas.library.api.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoan() {

        Book book = createBook();
        Loan savingLoan = createLoan();
        Loan savedLoan = createLoan();


        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Erro deve ser apresentado ao salvar um empréstimo de um livro já emprestado")
    public void loanedBookSave() {

        Book book = createBook();
        Loan savingLoan = createLoan();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable throwable = catchThrowable(() -> service.save(savingLoan));

        assertThat(throwable)
                .isInstanceOf(BussinessException.class)
                .hasMessage("Book already loaned");
    }


    @Test
    @DisplayName("Deve obter as infos de um emprestimo pelo ID")
    public void getLoan() {
        Long id = getId();
        Loan loan = createLoan();
        loan.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(loan.getId());
        assertThat(result.get().getBook().getId()).isEqualTo(loan.getBook().getId());
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);

    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoan() {

        Loan loan = createLoan();
        loan.setId(getId());
        loan.setReturned(true);

        when(service.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();

        verify(repository).save(loan);

    }


    @Test
    @DisplayName("Deve filtrar emprestimos pela propriedades")
    public void findLoansWithProps() throws Exception {

        //cenario

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("User").isbn("0123").build();
        Loan loan = createLoan();
        loan.setId(getId());

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> loanList = Arrays.asList(loan);

        Page<Loan> loanPages = new PageImpl<>(loanList, pageRequest, loanList.size());
        when(repository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(),Mockito.any(PageRequest.class)))
                .thenReturn(loanPages);

        //execucao
        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        //verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(loanList);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    private Long getId() {
        return Long.valueOf(11);
    }


    private Loan createLoan() {
        Book book = createBook();
        String customer = "Xurupita";
        return Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();
    }

    private Book createBook() {
        return Book.builder().id((long) 101).build();
    }


}