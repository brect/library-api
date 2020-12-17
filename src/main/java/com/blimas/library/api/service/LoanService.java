package com.blimas.library.api.service;

import com.blimas.library.api.dto.LoanFilterDTO;
import com.blimas.library.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO request, Pageable pageable);
}
