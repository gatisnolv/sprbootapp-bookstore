package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class AllFieldsNullExceptionTest {

    @Test
    public void test() {
        String message = "All fields were missing/null, the entry was not saved";
        Exception e = new AllFieldsNullException();
        assertEquals(message, e.getMessage());
    }

}
