package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class BookTest {

    @Test
    public void testNonNullGetPubLicationDate() {
        String date = "1949-06-08";
        Book book = new Book("1984", "George Orwell", "Secker & Warburg", date);
        assertEquals(date, book.getPublicationDate());
    }

    @Test
    public void testEmptyStringGetPubLicationDate() {
        String date = "";
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
    public void testAllFieldsEmpty1() {
        Book book = new Book(null, null, null, null);
        assertTrue(book.allFieldsEmpty());
    }

    @Test
    public void testAllFieldsEmpty2() {
        Book book = new Book("", "", "", "");
        assertTrue(book.allFieldsEmpty());
    }

    @Test
    public void testAllFieldsEmpty3() {
        Book book = new Book(null, null, null, "1984-01-01");
        assertFalse(book.allFieldsEmpty());
    }

}
