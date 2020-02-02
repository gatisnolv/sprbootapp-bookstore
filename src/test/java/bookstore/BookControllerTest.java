package bookstore;

import static bookstore.WebIntegrationTest.AGGREGATE_ROOT_INFIX;
import static bookstore.WebIntegrationTest.AUTHOR;
import static bookstore.WebIntegrationTest.EXISTING_ID;
import static bookstore.WebIntegrationTest.EXISTING_TITLE;
import static bookstore.WebIntegrationTest.NEW_ID;
import static bookstore.WebIntegrationTest.NONEXISTING_ID;
import static bookstore.WebIntegrationTest.NONEXISTING_TITLE;
import static bookstore.WebIntegrationTest.PUBLICATION_DATE;
import static bookstore.WebIntegrationTest.PUBLISHER;
import static bookstore.WebIntegrationTest.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext
public class BookControllerTest {

    static final String SELF_REL_NAME = "self";
    static final String AGGREGATE_ROOT_REL_NAME = "books";
    static final String SELF_REL_TEMPLATE = "/books/%s";

    private static final Book EXISTING_BOOK_1 = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
    private static final Book EXISTING_BOOK_2 = new Book("To Kill a Mockingbird", "Harper Lee", "J. B. Lippincott & Co.", "1960-11-07");
    private static final Book EXISTING_BOOK_3 = new Book("Animal Farm", "George Orwell", "Secker & Warburg", "1945-08-17");

    @Autowired
    private BookController controller;

    @BeforeClass
    public static void setupBookIds() {
        EXISTING_BOOK_1.setId(1L);
        EXISTING_BOOK_2.setId(2L);
        EXISTING_BOOK_3.setId(3L);
    }

    @Test
    public void getAllBooks() {
        List<Book> existingBooks = new ArrayList<>();
        existingBooks.add(EXISTING_BOOK_1);
        existingBooks.add(EXISTING_BOOK_2);
        existingBooks.add(EXISTING_BOOK_3);
        Resources<Resource<Book>> resources = controller.getAllBooks();
        assertEquals(AGGREGATE_ROOT_INFIX, resources.getLink(SELF_REL_NAME).getHref());
        Iterator<Resource<Book>> iterator = resources.getContent().iterator();
        for (int i = 0; i < resources.getContent().size(); i++) {
            Resource<Book> resource = iterator.next();
            assertEquals(existingBooks.get(i), resource.getContent());
            assertEquals(String.format(SELF_REL_TEMPLATE, i + 1), resource.getLink(SELF_REL_NAME).getHref());
            assertEquals(AGGREGATE_ROOT_INFIX, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
        }
    }

    @Test
    public void postBookWithNonNullFields() throws URISyntaxException {
        BookDTO newBookDTO = new BookDTO(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        ResponseEntity<?> responseEntity = controller.postNewBook(newBookDTO);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Resource<Book> resource = (Resource<Book>) responseEntity.getBody();
        assertNotNull(resource);
        assertTrue(bookEqualsBookDTO(resource.getContent(), newBookDTO));
        assertEquals(String.format(SELF_REL_TEMPLATE, NEW_ID), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_INFIX, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
    }

    @Test(expected = AllFieldsNullException.class)
    public void postBookWithAllNullFields() throws URISyntaxException {
        controller.postNewBook(new BookDTO(null, null, null, null));
    }

    @Test
    public void getOneExistingById() {
        Resource<Book> resource = controller.getOneBookById(EXISTING_ID);
        assertEquals(EXISTING_BOOK_1, resource.getContent());
        assertEquals(String.format(SELF_REL_TEMPLATE, EXISTING_ID), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_INFIX, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
    }

    @Test(expected = BookNotFoundException.class)
    public void getOneNonExistingById() {
        controller.getOneBookById(NONEXISTING_ID);
    }

    @Test
    public void getOneExistingByTitle() {
        Resource<Book> resource = controller.getOneBookByTitle(EXISTING_TITLE);
        assertEquals(EXISTING_BOOK_1, resource.getContent());
        assertEquals(String.format(SELF_REL_TEMPLATE, EXISTING_ID), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_INFIX, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());

    }

    @Test(expected = BookNotFoundException.class)
    public void getOneNonExistingByTitle() {
        controller.getOneBookByTitle(NONEXISTING_TITLE);
    }

    @Test
    public void replaceBookWithNonNullFields() throws URISyntaxException {
        BookDTO newBookDTO = new BookDTO(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        ResponseEntity<?> responseEntity = controller.replaceBookById(newBookDTO, EXISTING_ID);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Resource<Book> resource = (Resource<Book>) responseEntity.getBody();
        assertNotNull(resource);
        assertTrue(bookEqualsBookDTO(resource.getContent(), newBookDTO));
        assertEquals(String.format(SELF_REL_TEMPLATE, EXISTING_ID), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_INFIX, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
    }

    @Test(expected = BookNotFoundException.class)
    public void replaceNonExistingBookWithNonNullFields() throws URISyntaxException {
        controller.replaceBookById(new BookDTO(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE), NONEXISTING_ID);
    }

    @Test(expected = AllFieldsNullException.class)
    public void replaceBookWithNullFields() throws URISyntaxException {
        controller.replaceBookById(new BookDTO(null, null, null, null), EXISTING_ID);
    }

    @Test
    public void deleteExisting() {
        ResponseEntity<?> responseEntity = controller.deleteBookById(EXISTING_ID);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test(expected = BookNotFoundException.class)
    public void deleteNonExisting() {
        controller.deleteBookById(NONEXISTING_ID);
    }

    private boolean bookEqualsBookDTO(Book book, BookDTO bookDTO) {
        return book.getTitle().equals(bookDTO.getTitle()) && book.getAuthor().equals(bookDTO.getAuthor()) && book.getPublisher()
                .equals(bookDTO.getPublisher()) && book.getPublicationDate().equals(bookDTO.getPublicationDate());
    }
}
