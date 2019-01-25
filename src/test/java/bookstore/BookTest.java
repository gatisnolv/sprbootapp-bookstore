package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

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
        Book book = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
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
        String date = "1949-06-08";
        Book book = new Book("1984", "George Orwell", "Secker & Warburg", date);
        assertEquals(date, book.getPublicationDate());
    }

    @Test
    public void testNullGetPublicationDate() {
        Book book = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
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
        Book book = new Book(null, null, null, "1984-01-01");
        assertFalse(book.allFieldsNull());
    }

}
