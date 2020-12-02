package com.blimas.library.api.resource;


import com.blimas.library.api.dto.BookDTO;
import com.blimas.library.api.exception.ApiErrors;
import com.blimas.library.api.exception.BussinessException;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO request) {

        Book book = modelMapper.map(request, Book.class);

        book = service.save(book);

        return modelMapper.map(book, BookDTO.class);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }


    @ExceptionHandler(BussinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBussinessException(BussinessException exception) {
        return new ApiErrors(exception);
    }
}
