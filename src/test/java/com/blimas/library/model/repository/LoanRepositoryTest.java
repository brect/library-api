package com.blimas.library.model.repository;

import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.entity.Loan;
import com.blimas.library.api.model.repository.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se existe emprestimo nao devolvido")
    public void existsByBookAndNotReturned() {
        //cenario

        Loan loan = createAndPersistLoan();

        //execucao
        boolean exists = repository.existsByBookAndNotReturned(loan.getBook());

        //verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar emprestimo pelo isbn ou custumer")
    public void findByBookIsbnOrCustomer() {

        Loan loan = createAndPersistLoan();

        Page<Loan> result = repository.findByBookIsbnOrCustomer("001", "Usuario", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }


    private Loan createAndPersistLoan(){
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = createNewLoan(book);
        entityManager.persist(loan);

        return loan;
    }

    private Book createNewBook() {
        return Book.builder()
                .title("Meu Livro")
                .author("Autor")
                .isbn("001")
                .build();
    }
    private Loan createNewLoan(Book book) {
        return Loan.builder().book(book).customer("Usuario").loanDate(LocalDate.now()).build();
    }

}
