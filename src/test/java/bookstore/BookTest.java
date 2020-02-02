package bookstore;

import static bookstore.WebIntegrationTest.AUTHOR;
import static bookstore.WebIntegrationTest.PUBLICATION_DATE;
import static bookstore.WebIntegrationTest.PUBLISHER;
import static bookstore.WebIntegrationTest.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BookTest {

    @Test
    public void testNoArgsConstructor() {
        Book book = new Book();
        assertNull(book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getPublisher());
        assertNull(book.getPublicationDate());
    }

    @Test
    public void testNonNullGetPublicationDate() {
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        assertEquals(PUBLICATION_DATE, book.getPublicationDate());
    }

    @Test
    public void testNullGetPublicationDate() {
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        book.setPublicationDate(null);
        assertNull(book.getPublicationDate());
    }

    @Test
    public void testAllFieldsNull() {
        Book book = new Book(null, null, null, null);
        assertTrue(book.allFieldsNull());
    }

    @Test
    public void testNotAllFieldsNull() {
        Book book = new Book(null, null, null, PUBLICATION_DATE);
        assertFalse(book.allFieldsNull());
    }

}
