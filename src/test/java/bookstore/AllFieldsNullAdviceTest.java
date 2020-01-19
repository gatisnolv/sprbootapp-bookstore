package bookstore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AllFieldsNullAdviceTest {

    @Test
    public void test() {
        AllFieldsNullAdvice advice = new AllFieldsNullAdvice();
        AllFieldsNullException e = new AllFieldsNullException();
        assertEquals(WebIT.ALL_FIELDS_NULL_EXCEPTION_MESSAGE, advice.allFieldsNullHandler(e));
    }

}
