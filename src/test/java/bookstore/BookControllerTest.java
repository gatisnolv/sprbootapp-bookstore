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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    private static final String AGGREGATE_ROOT_INFIX = "/books";
    private static final String SELF_REL_NAME = "self";
    private static final String AGGREGATE_ROOT_REL_NAME = "books";
    private static final Long EXISTING_ID = 1L;
    private static final Long EXISTING_ID_2 = 2L;
    private static final Long NEW_ID = 4L;
    private static final Long NONEXISTING_ID = 10L;
    private static final String EXISTING_TITLE = "1984";
    private static final String NONEXISTING_TITLE = "2000";
    private static final String TITLE = "Metropolis";
    private static final String AUTHOR = "Thea von Harbou";
    private static final String PUBLISHER = "Illustriertes Blatt";
    private static final String PUBLICATION_DATE = "1925-01-01";
    private static final String SELF_REL_TEMPLATE = "/books/%s";
    private static final String AGGREGATE_ROOT_REL = "/books";

    private static final Book EXISTING_BOOK_1 = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
    private static final Book EXISTING_BOOK_2 = new Book("To Kill a Mockingbird", "Harper Lee", "J. B. Lippincott & Co.", "1960-11-07");
    private static final Book EXISTING_BOOK_3 = new Book("Animal Farm", "George Orwell", "Secker & Warburg", "1945-08-17");

    @Autowired
    private BookController controller;

    @Test
    public void getAllBooks() {
        EXISTING_BOOK_1.setId(1L);
        EXISTING_BOOK_2.setId(2L);
        EXISTING_BOOK_3.setId(3L);
        List<Book> existingBooks = new ArrayList<>();
        existingBooks.add(EXISTING_BOOK_1);
        existingBooks.add(EXISTING_BOOK_2);
        existingBooks.add(EXISTING_BOOK_3);
        Resources<Resource<Book>> resources = controller.getAllBooks();
        assertEquals(AGGREGATE_ROOT_INFIX, resources.getLink(SELF_REL_NAME).getHref());
        Iterator<Resource<Book>> iterator = resources.getContent().iterator();
        System.out.println(resources);
        for (Long i = 0L; i < existingBooks.size(); i++) {
            Resource<Book> resource = iterator.next();
            assertEquals(existingBooks.get(i.intValue()), resource.getContent());
            assertEquals(String.format(SELF_REL_TEMPLATE, i + 1), resource.getLink(SELF_REL_NAME).getHref());
            assertEquals(AGGREGATE_ROOT_REL, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
        }
    }

    @Test
    public void postBookWithNonNullFields() throws URISyntaxException {
        Book newBook = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        newBook.setId(NEW_ID);
        ResponseEntity<?> responseEntity = controller.postNewBook(newBook);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Resource<Book> resource = (Resource<Book>) responseEntity.getBody();
        assertNotNull(resource);
        assertEquals(newBook, resource.getContent());
        assertEquals(String.format(SELF_REL_TEMPLATE, NEW_ID), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_REL, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
    }

    @Test
    public void postBookWithAllNullFields() throws URISyntaxException {
        try {
            controller.postNewBook(new Book(null, null, null, null));
        } catch (RuntimeException e) {
            assertTrue(e instanceof AllFieldsNullException);
        }
    }

    @Test
    public void getOneExistingById() {
        Book existingBook = new Book(EXISTING_BOOK_1);
        existingBook.setId(1L);
        Resource<Book> resource = controller.getOneBookById(EXISTING_ID);
        assertEquals(existingBook, resource.getContent());
        assertEquals(String.format(SELF_REL_TEMPLATE, EXISTING_ID), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_REL, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
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
        Book existingBook = new Book(EXISTING_BOOK_1);
        existingBook.setId(1L);
        Resource<Book> resource = controller.getOneBookByTitle(EXISTING_TITLE);
        assertEquals(existingBook, resource.getContent());
        assertEquals(String.format(SELF_REL_TEMPLATE, EXISTING_ID), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_REL, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());

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
    public void replaceBookWithNonNullFields() throws URISyntaxException {
        Book newBook = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        ResponseEntity<?> responseEntity = controller.replaceBookById(newBook, EXISTING_ID_2);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Resource<Book> resource = (Resource<Book>) responseEntity.getBody();
        assertNotNull(resource);
        assertEquals(newBook, resource.getContent());
        assertEquals(String.format(SELF_REL_TEMPLATE, EXISTING_ID_2), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_REL, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
    }

    @Test
    public void replaceNonExistingBookWithNonNullFields() throws URISyntaxException {
        try {
            controller.replaceBookById(new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE), NONEXISTING_ID);
        } catch (RuntimeException e) {
            assertTrue(e instanceof BookNotFoundException);
        }
    }

    @Test
    public void replaceBookWithNullFields() throws URISyntaxException {
        try {
            controller.replaceBookById(new Book(null, null, null, null), EXISTING_ID_2);
        } catch (RuntimeException e) {
            assertTrue(e instanceof AllFieldsNullException);
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
