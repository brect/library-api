package com.blimas.library.api.service.impl;

import com.blimas.library.api.model.entity.Loan;
import com.blimas.library.api.model.repository.LoanRepository;
import com.blimas.library.api.service.LoanService;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {


    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return null;
    }
}
