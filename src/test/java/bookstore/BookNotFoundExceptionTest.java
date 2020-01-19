package bookstore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BookNotFoundExceptionTest {

    @Test
    public void testId() {
        Exception e = new BookNotFoundException(WebIntegrationTest.NONEXISTING_ID);
        assertEquals(String.format(
                WebIntegrationTest.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIntegrationTest.FIELD_ID)
                + WebIntegrationTest.NONEXISTING_ID, e.getMessage());
    }

    @Test
    public void testName() {
        Exception e = new BookNotFoundException(WebIntegrationTest.NONEXISTING_TITLE);
        assertEquals(String.format(WebIntegrationTest.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIntegrationTest.FIELD_TITLE)
                + String.format(WebIntegrationTest.PARENTHESIZE_TITLE_TEMPLATE, WebIntegrationTest.NONEXISTING_TITLE), e.getMessage());
    }

}
