package bookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext
public class WebIT {

    static final String AGGREGATE_ROOT_INFIX = "/books";
    static final String FIND_BY_TITLE_INFIX = "/title";
    static final Long EXISTING_ID = 1L;
    static final Long NEW_ID = 4L;
    static final Long NONEXISTING_ID = 10L;
    static final String EXISTING_TITLE = "1984";
    static final String NONEXISTING_TITLE = "Non Existing";
    static final String TITLE = "Metropolis";
    static final String AUTHOR = "Thea von Harbou";
    static final String PUBLISHER = "Illustriertes Blatt";
    static final String PUBLICATION_DATE = "1925-01-01";
    static final String FIELD_ID = "id";
    static final String FIELD_TITLE = "title";
    static final String FIELD_AUTHOR = "author";
    static final String FIELD_PUBLISHER = "publisher";
    static final String FIELD_PUBLICATION_DATE = "publicationDate";
    static final String BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE = "Could not find book with %s: ";
    static final String ALL_FIELDS_NULL_EXCEPTION_MESSAGE = "All fields were missing/null, the entry was not saved";
    static final String PARENTHESIZE_TITLE_TEMPLATE = "'%s'";
    static final String JSON_PATH_ROOT_PREFIX = "$.";
    static final String JSON_BOOKLIST_PATH_INFIX = "_embedded.bookList";
    static final String JSON_AGGREGATE_ACCESSOR_INFIX = "[*].";

    private static final Book EXISTING_BOOK_1 = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
    private static final Book EXISTING_BOOK_2 = new Book("To Kill a Mockingbird", "Harper Lee", "J. B. Lippincott & Co.", "1960-11-07");
    private static final Book EXISTING_BOOK_3 = new Book("Animal Farm", "George Orwell", "Secker & Warburg", "1945-08-17");

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    private boolean initialized;

    @BeforeClass
    public static void setupBookIds() {
        EXISTING_BOOK_1.setId(1L);
        EXISTING_BOOK_2.setId(2L);
        EXISTING_BOOK_3.setId(3L);
    }

    @Before
    public void setup() {
        if (!initialized) {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
            initialized = true;
        }
    }

    @Test
    public void getAllBooks() throws Exception {
        List<Book> bookList = new LinkedList<>();
        bookList.add(EXISTING_BOOK_1);
        bookList.add(EXISTING_BOOK_2);
        bookList.add(EXISTING_BOOK_3);
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + "/")
                .accept(MediaTypes.HAL_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX).exists())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX).value(hasSize(3)))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX + FIELD_ID).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX + FIELD_TITLE).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX + FIELD_AUTHOR).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX + FIELD_PUBLISHER).value(not(hasItem(nullValue()))))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + JSON_BOOKLIST_PATH_INFIX + JSON_AGGREGATE_ACCESSOR_INFIX + FIELD_PUBLICATION_DATE).value(not(hasItem(nullValue()))));
    }

    @Test
    public void postBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        mockMvc.perform(post(AGGREGATE_ROOT_INFIX + "/")
                .accept(MediaTypes.HAL_JSON_UTF8)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_ID).value(NEW_ID))
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
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_ID).value(EXISTING_BOOK_1.getId()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(EXISTING_BOOK_1.getTitle()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(EXISTING_BOOK_1.getAuthor()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(EXISTING_BOOK_1.getPublisher()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(EXISTING_BOOK_1.getPublicationDate()));
    }

    @Test
    public void getOneNonExistingById() throws Exception {
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + "/" + NONEXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID));
    }

    @Test
    public void getOneExistingByTitle() throws Exception {
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + FIND_BY_TITLE_INFIX + "/" + EXISTING_TITLE)
                .accept(MediaTypes.HAL_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_TITLE).value(EXISTING_BOOK_1.getTitle()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_AUTHOR).value(EXISTING_BOOK_1.getAuthor()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLISHER).value(EXISTING_BOOK_1.getPublisher()))
                .andExpect(jsonPath(JSON_PATH_ROOT_PREFIX + FIELD_PUBLICATION_DATE).value(EXISTING_BOOK_1.getPublicationDate()));
    }

    @Test
    public void getOneNonExistingByTitle() throws Exception {
        mockMvc.perform(get(AGGREGATE_ROOT_INFIX + FIND_BY_TITLE_INFIX + "/" + NONEXISTING_TITLE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_TITLE) + String.format(PARENTHESIZE_TITLE_TEMPLATE, NONEXISTING_TITLE)));
    }

    @Test
    public void replaceBookWithNonNullFields() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
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
        mockMvc.perform(delete(AGGREGATE_ROOT_INFIX + "/" + NONEXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID));
    }
}
