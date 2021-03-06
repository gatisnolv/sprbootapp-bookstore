package bookstore;

import static bookstore.WebIntegrationTest.AGGREGATE_ROOT_INFIX;
import static bookstore.WebIntegrationTest.ALL_FIELDS_NULL_EXCEPTION_MESSAGE;
import static bookstore.WebIntegrationTest.AUTHOR;
import static bookstore.WebIntegrationTest.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE;
import static bookstore.WebIntegrationTest.EXISTING_ID;
import static bookstore.WebIntegrationTest.EXISTING_TITLE;
import static bookstore.WebIntegrationTest.FIELD_AUTHOR;
import static bookstore.WebIntegrationTest.FIELD_ID;
import static bookstore.WebIntegrationTest.FIELD_PUBLICATION_DATE;
import static bookstore.WebIntegrationTest.FIELD_PUBLISHER;
import static bookstore.WebIntegrationTest.FIELD_TITLE;
import static bookstore.WebIntegrationTest.FIND_BY_TITLE_INFIX;
import static bookstore.WebIntegrationTest.JSON_AGGREGATE_ACCESSOR_INFIX;
import static bookstore.WebIntegrationTest.JSON_BOOKLIST_PATH_INFIX;
import static bookstore.WebIntegrationTest.JSON_PATH_ROOT_PREFIX;
import static bookstore.WebIntegrationTest.NONEXISTING_ID;
import static bookstore.WebIntegrationTest.NONEXISTING_TITLE;
import static bookstore.WebIntegrationTest.PARENTHESIZE_TITLE_TEMPLATE;
import static bookstore.WebIntegrationTest.PUBLICATION_DATE;
import static bookstore.WebIntegrationTest.PUBLISHER;
import static bookstore.WebIntegrationTest.TITLE;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

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
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + "/")
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX).exists())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX).value(hasSize(3)))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX + FIELD_ID)
                        .value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX
                        + FIELD_TITLE).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX
                        + FIELD_AUTHOR).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX
                        + FIELD_PUBLISHER).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX
                        + FIELD_PUBLICATION_DATE).value(not(hasItem(nullValue()))));
    }

    @Test
    public void postBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookDTO bookDTO = new BookDTO(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        Book book = new Book(bookDTO);
        book.setId(NONEXISTING_ID);
        when(repository.save(ArgumentMatchers.any(Book.class))).thenReturn(book);
        mockMvc.perform(post(AGGREGATE_ROOT_INFIX + "/")
                .accept(MediaTypes.HAL_JSON_UTF8)
                .content(objectMapper.writeValueAsString(bookDTO))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_ID).value(NONEXISTING_ID))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(TITLE))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(AUTHOR))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(PUBLISHER))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(PUBLICATION_DATE));
    }

    @Test
    public void postBookWithNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(null, null, null, null);
        mockMvc.perform(post(AGGREGATE_ROOT_INFIX + "/")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ALL_FIELDS_NULL_EXCEPTION_MESSAGE));
    }

    @Test
    public void getOneExistingById() throws Exception {
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        book.setId(EXISTING_ID);
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(book));
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_ID).value(EXISTING_ID))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(TITLE))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(AUTHOR))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(PUBLISHER))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(PUBLICATION_DATE));
    }

    @Test
    public void getOneNonExistingById() throws Exception {
        when(repository.findById(NONEXISTING_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + "/" + NONEXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID));
    }

    @Test
    public void getOneExistingByTitle() throws Exception {
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        when(repository.findByTitleIgnoreCase(EXISTING_TITLE)).thenReturn(book);
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + FIND_BY_TITLE_INFIX + "/" + EXISTING_TITLE)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(TITLE))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(AUTHOR))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(PUBLISHER))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(PUBLICATION_DATE));
    }

    @Test
    public void getOneNonExistingByTitle() throws Exception {
        when(repository.findByTitleIgnoreCase(NONEXISTING_TITLE)).thenReturn(null);
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + FIND_BY_TITLE_INFIX + "/" + NONEXISTING_TITLE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_TITLE) +
                        String.format(PARENTHESIZE_TITLE_TEMPLATE, NONEXISTING_TITLE)));
    }

    @Test
    public void replaceBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        book.setId(EXISTING_ID);
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);
        mockMvc.perform(put(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_ID).value(EXISTING_ID))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(TITLE))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(AUTHOR))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(PUBLISHER))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(PUBLICATION_DATE));
    }

    @Test
    public void replaceNonExistingBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        when(repository.findById(NONEXISTING_ID)).thenReturn(Optional.empty());
        mockMvc.perform(put(AGGREGATE_ROOT_INFIX + "/" + NONEXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID));
    }

    @Test
    public void replaceBookWithAllNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(null, null, null, null);
        mockMvc.perform(put(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ALL_FIELDS_NULL_EXCEPTION_MESSAGE));
    }

    @Test
    public void deleteExisting() throws Exception {
        mockMvc.perform(delete(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));
    }

    @Test
    public void deleteNonExisting() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(NONEXISTING_ID);
        mockMvc.perform(delete(AGGREGATE_ROOT_INFIX + "/" + NONEXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID));
    }
}
