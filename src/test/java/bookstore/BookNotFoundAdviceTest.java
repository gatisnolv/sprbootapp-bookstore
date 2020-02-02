package bookstore;

import static bookstore.WebIntegrationTest.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE;
import static bookstore.WebIntegrationTest.FIELD_ID;
import static bookstore.WebIntegrationTest.NONEXISTING_ID;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BookNotFoundAdviceTest {

    @Test
    public void test() {
        BookNotFoundAdvice advice = new BookNotFoundAdvice();
        BookNotFoundException e = new BookNotFoundException(NONEXISTING_ID);
        assertEquals(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID, advice.bookNotFoundHandler(e));
    }

}
