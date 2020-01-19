package bookstore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BookNotFoundAdviceTest {

    @Test
    public void test() {
        BookNotFoundAdvice advice = new BookNotFoundAdvice();
        BookNotFoundException e = new BookNotFoundException(WebIntegrationTest.NONEXISTING_ID);
        assertEquals(String.format(
                WebIntegrationTest.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIntegrationTest.FIELD_ID)
                + WebIntegrationTest.NONEXISTING_ID, advice.bookNotFoundHandler(e));
    }

}
