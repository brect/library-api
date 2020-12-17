package com.blimas.library.api.model.repository;

import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when (count(loan.id) > 0) then true else false end  from Loan loan where loan.book = :book and (loan.returned is null or loan.returned is not true)")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    @Query(value = " select l from Loan as l join l.book as b where b.isbn = :isbn or l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn,
                                        @Param("customer") String customer,
                                        Pageable pageable);
}
