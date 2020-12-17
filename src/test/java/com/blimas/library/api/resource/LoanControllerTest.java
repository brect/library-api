package com.blimas.library.api.resource;

import com.blimas.library.api.dto.BookDTO;
import com.blimas.library.api.dto.LoanDTO;
import com.blimas.library.api.dto.LoanFilterDTO;
import com.blimas.library.api.dto.ReturnedLoanDTO;
import com.blimas.library.api.exception.BussinessException;
import com.blimas.library.api.model.entity.Book;
import com.blimas.library.api.model.entity.Loan;
import com.blimas.library.api.service.BookService;
import com.blimas.library.api.service.LoanService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static String LOAN_API = "/api/loans";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoan() throws Exception {

        LoanDTO LoanDTO = com.blimas.library.api.dto.LoanDTO.builder().isbn("123").customer("Xurupita").build();
        String json = new ObjectMapper().writeValueAsString(LoanDTO);

        long id = 101;
        Book book = Book.builder().id(id).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(id).customer("Xurupita").book(book).loanDate(LocalDate.now()).build();

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("101"));

    }


    @Test
    @DisplayName("Deve retornar erro ao fazer um emprestimo de um livro inexistente")
    public void invalidIsbnCreateLoan() throws Exception {
        LoanDTO LoanDTO = com.blimas.library.api.dto.LoanDTO.builder().isbn("123").customer("Xurupita").build();
        String json = new ObjectMapper().writeValueAsString(LoanDTO);
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));

    }

    @Test
    @DisplayName("Deve retornar erro ao fazer um emprestimo de um livro emprestado")
    public void loanedBookErrorOnCreateLoan() throws Exception {
        LoanDTO LoanDTO = com.blimas.library.api.dto.LoanDTO.builder().isbn("123").customer("Xurupita").build();
        String json = new ObjectMapper().writeValueAsString(LoanDTO);

        long id = 101;
        Book book = Book.builder().id(id).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BussinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));

    }

    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBook() throws Exception {
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

        Loan loan = Loan.builder().id((long) 11).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mockMvc.perform(patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar 404 quando um livro devolvido for inexistente")
    public void returnInexistentBook() throws Exception {
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        mockMvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void findLoan() throws Exception {
        Long id = Long.valueOf(101);

        Loan loan = createNewLoan();
        loan.setId(id);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        String query = String.format("?isbn=%s&customer=%s&page=0&size=100", loan.getBook().getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(query))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }


    private Loan createNewLoan() {
        Book book = createBook();
        String customer = "Xurupita";
        return Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();
    }

    private Book createBook() {
        return Book.builder()
                .id((long) 101)
                .title("Meu Livro")
                .author("Autor")
                .isbn("001")
                .build();
    }



}
