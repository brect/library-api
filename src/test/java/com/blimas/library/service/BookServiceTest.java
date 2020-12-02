package com.blimas.library.service;

import com.blimas.library.api.exception.BussinessException;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.repository.BookRepository;
import com.blimas.library.api.service.BookService;
import com.blimas.library.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }


    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBook() {

        //cenario
        Book book = createNewBook();

        Mockito.when(repository.save(book))
                .thenReturn(
                        createNewBook()
                );

        //execucao
        Book savedBook = service.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo("Autor");
        assertThat(savedBook.getTitle()).isEqualTo("Meu Livro");
        assertThat(savedBook.getIsbn()).isEqualTo("001");

    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void shouldNotSaveABookWithDuplicatedISBN() {

        //cenario
        Book book = createNewBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable throwable = Assertions.catchThrowable(() -> service.save(book));

        //verificacoes
        assertThat(throwable).isInstanceOf(BussinessException.class).hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private Book createNewBook() {
        return Book.builder()
                .id((long) 101)
                .title("Meu Livro")
                .author("Autor")
                .isbn("001")
                .build();
    }
}
