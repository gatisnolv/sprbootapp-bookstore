package bookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class WebMockTest {

    private static final Book EXISTING_BOOK_1 = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
    private static final Book EXISTING_BOOK_2 = new Book("To Kill a Mockingbird", "Harper Lee", "J. B. Lippincott & Co.", "1960-11-07");
    private static final Book EXISTING_BOOK_3 = new Book("Animal Farm", "George Orwell", "Secker & Warburg", "1945-08-17");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository repository;

    @SpyBean
    private BookResourceAssembler assembler;

    @Test
    public void getAllBooks() throws Exception {
        EXISTING_BOOK_1.setId(1L);
        EXISTING_BOOK_2.setId(2L);
        EXISTING_BOOK_3.setId(3L);
        List<Book> bookList = new LinkedList<>();
        bookList.add(EXISTING_BOOK_1);
        bookList.add(EXISTING_BOOK_2);
        bookList.add(EXISTING_BOOK_3);
        when(repository.findAll()).thenReturn(bookList);
        mockMvc.perform(get(WebIT.AGGREGATE_ROOT_INFIX + "/")
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.JSON_BOOKLIST_PATH_INFIX).exists())
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.JSON_BOOKLIST_PATH_INFIX).value(hasSize(3)))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.JSON_BOOKLIST_PATH_INFIX + WebIT.JSON_AGGREGATE_ACCESSOR_INFIX + WebIT.FIELD_ID).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.JSON_BOOKLIST_PATH_INFIX + WebIT.JSON_AGGREGATE_ACCESSOR_INFIX + WebIT.FIELD_TITLE).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.JSON_BOOKLIST_PATH_INFIX + WebIT.JSON_AGGREGATE_ACCESSOR_INFIX + WebIT.FIELD_AUTHOR).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.JSON_BOOKLIST_PATH_INFIX + WebIT.JSON_AGGREGATE_ACCESSOR_INFIX + WebIT.FIELD_PUBLISHER).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.JSON_BOOKLIST_PATH_INFIX + WebIT.JSON_AGGREGATE_ACCESSOR_INFIX + WebIT.FIELD_PUBLICATION_DATE).value(not(hasItem(nullValue()))));
    }

    @Test
    public void postBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        book.setId(WebIT.NONEXISTING_ID);
        when(repository.save(book)).thenReturn(book);
        mockMvc.perform(post(WebIT.AGGREGATE_ROOT_INFIX + "/")
                .accept(MediaTypes.HAL_JSON_UTF8)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_ID).value(WebIT.NONEXISTING_ID))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_TITLE).value(WebIT.TITLE))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_AUTHOR).value(WebIT.AUTHOR))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLISHER).value(WebIT.PUBLISHER))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLICATION_DATE).value(WebIT.PUBLICATION_DATE));
    }

    @Test
    public void postBookWithNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(null, null, null, null);
        mockMvc.perform(post(WebIT.AGGREGATE_ROOT_INFIX + "/")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(WebIT.ALL_FIELDS_NULL_EXCEPTION_MESSAGE));
    }

    @Test
    public void getOneExistingById() throws Exception {
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        book.setId(WebIT.EXISTING_ID);
        when(repository.findById(WebIT.EXISTING_ID)).thenReturn(Optional.of(book));
        mockMvc.perform(get(WebIT.AGGREGATE_ROOT_INFIX + "/" + WebIT.EXISTING_ID)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_ID).value(WebIT.EXISTING_ID))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_TITLE).value(WebIT.TITLE))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_AUTHOR).value(WebIT.AUTHOR))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLISHER).value(WebIT.PUBLISHER))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLICATION_DATE).value(WebIT.PUBLICATION_DATE));
    }

    @Test
    public void getOneNonExistingById() throws Exception {
        when(repository.findById(WebIT.NONEXISTING_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get(WebIT.AGGREGATE_ROOT_INFIX + "/" + WebIT.NONEXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(WebIT.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIT.FIELD_ID) + WebIT.NONEXISTING_ID));
    }

    @Test
    public void getOneExistingByTitle() throws Exception {
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        when(repository.findByTitleIgnoreCase(WebIT.EXISTING_TITLE)).thenReturn(book);
        mockMvc.perform(get(WebIT.AGGREGATE_ROOT_INFIX + WebIT.FIND_BY_TITLE_INFIX + "/" + WebIT.EXISTING_TITLE)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_TITLE).value(WebIT.TITLE))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_AUTHOR).value(WebIT.AUTHOR))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLISHER).value(WebIT.PUBLISHER))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLICATION_DATE).value(WebIT.PUBLICATION_DATE));
    }

    @Test
    public void getOneNonExistingByTitle() throws Exception {
        when(repository.findByTitleIgnoreCase(WebIT.NONEXISTING_TITLE)).thenReturn(null);
        mockMvc.perform(get(WebIT.AGGREGATE_ROOT_INFIX + WebIT.FIND_BY_TITLE_INFIX + "/" + WebIT.NONEXISTING_TITLE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(WebIT.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIT.FIELD_TITLE) + String.format(WebIT.PARENTHESIZE_TITLE_TEMPLATE, WebIT.NONEXISTING_TITLE)));
    }

    @Test
    public void replaceBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        book.setId(WebIT.EXISTING_ID);
        when(repository.findById(WebIT.EXISTING_ID)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);
        mockMvc.perform(put(WebIT.AGGREGATE_ROOT_INFIX + "/" + WebIT.EXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_ID).value(WebIT.EXISTING_ID))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_TITLE).value(WebIT.TITLE))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_AUTHOR).value(WebIT.AUTHOR))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLISHER).value(WebIT.PUBLISHER))
                .andExpect(jsonPath(WebIT.JSON_PATH_ROOT_PREFIX + WebIT.FIELD_PUBLICATION_DATE).value(WebIT.PUBLICATION_DATE));
    }

    @Test
    public void replaceNonExistingBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        when(repository.findById(WebIT.NONEXISTING_ID)).thenReturn(Optional.empty());
        mockMvc.perform(put(WebIT.AGGREGATE_ROOT_INFIX + "/" + WebIT.NONEXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(WebIT.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIT.FIELD_ID) + WebIT.NONEXISTING_ID));
    }

    @Test
    public void replaceBookWithAllNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(null, null, null, null);
        mockMvc.perform(put(WebIT.AGGREGATE_ROOT_INFIX + "/" + WebIT.EXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(WebIT.ALL_FIELDS_NULL_EXCEPTION_MESSAGE));
    }

    @Test
    public void deleteExisting() throws Exception {
        mockMvc.perform(delete(WebIT.AGGREGATE_ROOT_INFIX + "/" + WebIT.EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));

    }

    @Test
    public void deleteNonExisting() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(WebIT.NONEXISTING_ID);
        mockMvc.perform(delete(WebIT.AGGREGATE_ROOT_INFIX + "/" + WebIT.NONEXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(WebIT.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIT.FIELD_ID) + WebIT.NONEXISTING_ID));
    }
}
