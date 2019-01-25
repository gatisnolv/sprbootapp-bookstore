package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class AllFieldsNullAdviceTest {

    @Test
    public void test() {
        AllFieldsNullAdvice advice = new AllFieldsNullAdvice();
        String message = "All fields were missing/null, the entry was not saved";
        AllFieldsNullException e = new AllFieldsNullException();
        assertEquals(message, advice.allFieldsNullHandler(e));
    }

}
