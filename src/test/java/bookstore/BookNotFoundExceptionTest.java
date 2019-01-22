package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class BookNotFoundExceptionTest {

    @Test
    public void testId() {
        Long id = new Long(7);
        String message = "Could not find book with id: " + id;
        Exception e = new BookNotFoundException(id);
        assertEquals(message, e.getMessage());
    }

    @Test
    public void testName() {
        String name = "Test Name";
        String message = "Could not find book with name: '" + name + "'";
        Exception e = new BookNotFoundException(name);
        assertEquals(message, e.getMessage());
    }

}