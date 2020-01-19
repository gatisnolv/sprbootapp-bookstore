package bookstore;

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
    public void testCopyConstructor() {
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        book.setId(1L);
        Book copy = new Book(book);
        assertEquals(book.getId(), copy.getId());
        assertEquals(book.getTitle(), copy.getTitle());
        assertEquals(book.getAuthor(), copy.getAuthor());
        assertEquals(book.getPublisher(), copy.getPublisher());
        assertEquals(book.getPublicationDate(), copy.getPublicationDate());
    }

    @Test
    public void testNonNullGetPubLicationDate() {
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        assertEquals(WebIT.PUBLICATION_DATE, book.getPublicationDate());
    }

    @Test
    public void testNullGetPublicationDate() {
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
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
        Book book = new Book(null, null, null, WebIT.PUBLICATION_DATE);
        assertFalse(book.allFieldsNull());
    }

}
