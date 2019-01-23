package bookstore;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Ignore;
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

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class WebMockTest {

    private static final String AGGREGATE_ROOT_INFIX = "/books";
    private static final String FIND_BY_TITLE_INFIX = "/title";
    private static final String SELF_LINK = "self";
    private static final Long EXISTING_ID = 1L;
    private static final Long NONEXISTING_ID = 10L;
    private static final String EXISTING_TITLE = "1984";
    //    private static final String NONEXISTING_TITLE = "2000";
    private static final String NONEXISTING_TITLE = "Non Existing";
    private static final String TITLE = "Metropolis";
    private static final String AUTHOR = "Thea von Harbou";
    private static final String PUBLISHER = "Illustriertes Blatt";
    private static final String PUBLICATION_DATE = "1925-01-01";
    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_PUBLISHER = "publisher";
    private static final String FIELD_PUBLICATION_DATE = "publicationDate";
    private static final String BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE = "Could not find book with %s: ";
    private static final String ALL_FIELDS_EMPTY_EXCEPTION_MESSAGE = "All fields were missing/empty, the entry was not saved";
    private static final String PARENTHESIZE_TITLE_TEMPLATE = "'%s'";
    private static final String JSON_PATH_ROOT_PREFIX = "$.";
    private static final String JSON_BOOKLIST_PATH_INFIX = "_embedded.bookList";
    private static final String JSON_AGGREGATE_ACCESSOR_INFIX = "[*].";


    private static final Book book1 = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
    private static final Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", "J. B. Lippincott & Co.", "1960-11-07");
    private static final Book book3 = new Book("Animal Farm", "George Orwell", "Secker & Warburg", "1945-08-17");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository repository;

    @SpyBean
    private BookResourceAssembler assembler;

    @Test
    public void getAllBooks() throws Exception {//FIXME figure out how to check values ar not-null using the [*] in json path
//        book1.setId(1L);
//        book2.setId(2L);
//        book3.setId(3L);
        List bookList = new LinkedList<Book>();
        bookList.add(book1);
        bookList.add(book2);
        bookList.add(book3);
        when(repository.findAll()).thenReturn(bookList);
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + "/")
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX).exists())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX + FIELD_ID).value(Matchers.nullValue()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX).value(hasSize(3)))
        ;
    }

    @Test
    public void postBookWithNonEmptyFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        book.setId(NONEXISTING_ID);//TODO q: is this the best place for this?

        when(repository.save(book)).thenReturn(book);
        mockMvc.perform(post(AGGREGATE_ROOT_INFIX + "/")
//                .accept(MediaTypes.HAL_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(book))
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
    public void postBookWithEmptyFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(null, null, null, null);
        mockMvc.perform(post(AGGREGATE_ROOT_INFIX + "/")
//                .accept(MediaTypes.HAL_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format(ALL_FIELDS_EMPTY_EXCEPTION_MESSAGE)));
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
        when(repository.findByTitle(EXISTING_TITLE)).thenReturn(book);
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + FIND_BY_TITLE_INFIX + "/" + EXISTING_TITLE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(TITLE))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(AUTHOR))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(PUBLISHER))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(PUBLICATION_DATE));
    }

    @Test
    public void getOneNonExistingByTitle() throws Exception {
        when(repository.findByTitle(NONEXISTING_TITLE)).thenReturn(null);
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + FIND_BY_TITLE_INFIX + "/" + NONEXISTING_TITLE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_TITLE) + String.format(PARENTHESIZE_TITLE_TEMPLATE, NONEXISTING_TITLE)));
    }

    @Test
    public void replaceBookWithNonEmptyFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        book.setId(EXISTING_ID);
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);
        mockMvc.perform(put(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_ID).value(EXISTING_ID))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(TITLE))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(AUTHOR))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(PUBLISHER))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(PUBLICATION_DATE));
    }

    @Test
    public void replaceNonExistingBookWithNonEmptyFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        when(repository.findById(NONEXISTING_ID)).thenReturn(Optional.empty());
        mockMvc.perform(put(AGGREGATE_ROOT_INFIX + "/" + NONEXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID));
    }

    @Test
    public void replaceBookWithEmptyFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(null, null, null, null);
        mockMvc.perform(put(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ALL_FIELDS_EMPTY_EXCEPTION_MESSAGE));
    }

    @Test
    public void deleteExisting() throws Exception {
        mockMvc.perform(delete(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

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
