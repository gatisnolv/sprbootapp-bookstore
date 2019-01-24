package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    private static final String AGGREGATE_ROOT_INFIX = "/books";
    private static final String SELF_LINK = "self";
    private static final Long EXISTING_ID = 1L;
    private static final Long EXISTING_ID_2 = 2L;
    private static final Long NONEXISTING_ID = 10L;
    private static final String EXISTING_TITLE = "1984";
    private static final String NONEXISTING_TITLE = "2000";
    private static final String TITLE = "Metropolis";
    private static final String AUTHOR = "Thea von Harbou";
    private static final String PUBLISHER = "Illustriertes Blatt";
    private static final String PUBLICATION_DATE = "1925-01-01";

    @Autowired
    private BookController controller;

    @Test
    public void getAllBooks() {
        Resources<Resource<Book>> resources = controller.getAllBooks();
        for (Resource<Book> resource : resources.getContent()) {
            assertNotNull(resource);
        }
        assertEquals(AGGREGATE_ROOT_INFIX, resources.getLink(SELF_LINK).getHref());
    }

    @Test
    public void postBookWithNonEmptyFields() throws URISyntaxException {
        Book newBook = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        ResponseEntity<?> responseEntity = controller.postNewBook(newBook);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(responseEntity.toString().contains(newBook.toString()));
    }

    @Test
    public void postBookWithEmptyFields() throws URISyntaxException {
        try {
            controller.postNewBook(new Book(null, null, null, null));
        } catch (RuntimeException e) {
            assertTrue(e instanceof AllFieldsEmptyException);
        }
    }

    @Test
    public void getOneExistingById() {
        Resource<Book> resource = controller.getOneBookById(EXISTING_ID);
        assertEquals(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID, resource.getLink(SELF_LINK).getHref());
    }

    @Test
    public void getOneNonExistingById() {
        try {
            controller.getOneBookById(NONEXISTING_ID);
        } catch (RuntimeException e) {
            assertTrue(e instanceof BookNotFoundException);
        }
    }

    @Test
    public void getOneExistingByTitle() {
        Resource<Book> resource = controller.getOneBookByTitle(EXISTING_TITLE);
        assertEquals(AGGREGATE_ROOT_INFIX + "/" + EXISTING_ID, resource.getLink(SELF_LINK).getHref());
    }

    @Test
    public void getOneNonExistingByTitle() {
        try {
            controller.getOneBookByTitle(NONEXISTING_TITLE);
        } catch (RuntimeException e) {
            assertTrue(e instanceof BookNotFoundException);
        }
    }

    @Test
    public void replaceBookWithNonEmptyFields() throws URISyntaxException {
        ResponseEntity<?> responseEntity = controller.replaceBookById(new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE), EXISTING_ID);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void replaceNonExistingBookWithNonEmptyFields() throws URISyntaxException {
        try {
            controller.replaceBookById(new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE), NONEXISTING_ID);
        } catch (RuntimeException e) {
            assertTrue(e instanceof BookNotFoundException);
        }
    }

    @Test
    public void replaceBookWithEmptyFields() throws URISyntaxException {
        try {
            controller.replaceBookById(new Book(null, null, null, null), EXISTING_ID);
        } catch (RuntimeException e) {
            assertTrue(e instanceof AllFieldsEmptyException);
        }
    }

    @Test
    public void deleteExisting() {
        ResponseEntity<?> responseEntity = controller.deleteBookById(EXISTING_ID_2);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void deleteNonExisting() {
        try {
            controller.deleteBookById(NONEXISTING_ID);
        } catch (RuntimeException e) {
            assertTrue(e instanceof BookNotFoundException);
        }
    }

}
