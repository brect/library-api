package com.blimas.library.api.resource;

import com.blimas.library.api.dto.BookDTO;
import com.blimas.library.api.exception.BussinessException;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBook() throws Exception {

        BookDTO bookDTO = createNewBook();

        Book book = Book.builder()
                .id((long) 101)
                .title("Meu Livro")
                .author("Autor")
                .isbn("001")
                .build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(book);

        String contentJson = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(contentJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(101))
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));

    }

    private BookDTO createNewBook() {
        return BookDTO.builder()
                .title("Meu Livro")
                .author("Autor")
                .isbn("001")
                .build();
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar criar um livro inválido.")
    public void createInvalidBook() throws Exception {

        String contentJson = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(contentJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }


    @Test
    @DisplayName("Deve lancar erro ao tentar criar um livro com isbn duplicado.")
    public void createBookWithDuplicatedIsbn() throws Exception {

        String contentJson = new ObjectMapper().writeValueAsString(createNewBook());

        String msgErro = "Isbn já cadastrado";

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BussinessException(msgErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(contentJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(msgErro));

    }

}
