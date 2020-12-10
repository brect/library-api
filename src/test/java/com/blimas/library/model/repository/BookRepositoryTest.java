package com.blimas.library.model.repository;

import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir livro na base com isbn informado")
    public void returnTrueWhenIsbnExists() {
        //cenario
        String isbn = "001";

        Book book = createNewBook();
        entityManager.persist(book);

        //execucao
        boolean isExistsIsbn = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(isExistsIsbn).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir livro na base com isbn informado")
    public void returnFalseWhenIsbnDontExists() {
        //cenario
        String isbn = "002";

        Book book = createNewBook();
        entityManager.persist(book);

        //execucao
        boolean isExistsIsbn = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(isExistsIsbn).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void findBookById() {

        Book book = createNewBook();
        entityManager.persist(book);

        //execucao
        Optional<Book> foundBook = repository.findById(book.getId());

        //verificacao
        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBook() {

        Book book = createNewBook();
        entityManager.persist(book);

        //execucao
        Book savedBook = repository.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBook() {

        Book book = createNewBook();
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());
        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();
    }

    private Book createNewBook() {
        return Book.builder()
                .title("Meu Livro")
                .author("Autor")
                .isbn("001")
                .build();
    }

}
