package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class AllFieldsEmptyAdviceTest {

    @Test
    public void test() {
        AllFieldsEmptyAdvice advice = new AllFieldsEmptyAdvice();
        String message = "All fields were missing/empty, the entry was not saved";
        AllFieldsEmptyException e = new AllFieldsEmptyException();
        assertEquals(message, advice.allFieldsEmptyHandler(e));
    }

}
