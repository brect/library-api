package com.blimas.library.api.service.impl;

import com.blimas.library.api.exception.BussinessException;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.repository.BookRepository;
import com.blimas.library.api.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {


    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())){
            throw new BussinessException("Isbn já cadastrado");
        }
        return repository.save(book);
    }
}
