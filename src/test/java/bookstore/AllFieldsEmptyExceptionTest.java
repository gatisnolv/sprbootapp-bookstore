package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class AllFieldsEmptyExceptionTest {

    @Test
    public void test() {
        String message = "All fields were missing/empty, the entry was not saved";
        Exception e = new AllFieldsEmptyException();
        assertEquals(message, e.getMessage());
    }

}
