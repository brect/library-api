package com.blimas.library.api.resource;


import com.blimas.library.api.dto.BookDTO;
import com.blimas.library.api.exception.ApiErrors;
import com.blimas.library.api.exception.BussinessException;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    public BookDTO getBook(@PathVariable Long id) {
        return service
                .getById(id)
                .map(book -> modelMapper
                        .map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDTO> findBook(BookDTO book, Pageable pageable) {
        Book bookMap = modelMapper.map(book, Book.class);
        Page<Book> booksResult = service.find(bookMap, pageable);

        List<BookDTO> bookList = booksResult.getContent().stream().map(entity -> modelMapper.map(entity, BookDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(bookList, pageable, booksResult.getTotalElements());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO request) {

        Book book = modelMapper.map(request, Book.class);

        book = service.save(book);

        return modelMapper.map(book, BookDTO.class);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }


    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable Long id, BookDTO request) {
        return service.getById(id).map(book -> {

                    book.setTitle(request.getTitle());
                    book.setAuthor(request.getAuthor());
                    book = service.update(book);

                    return modelMapper.map(book, BookDTO.class);

                }
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


    }

}
