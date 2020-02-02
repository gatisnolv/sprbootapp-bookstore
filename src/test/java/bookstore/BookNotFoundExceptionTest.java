package bookstore;

import static bookstore.WebIntegrationTest.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE;
import static bookstore.WebIntegrationTest.FIELD_ID;
import static bookstore.WebIntegrationTest.FIELD_TITLE;
import static bookstore.WebIntegrationTest.NONEXISTING_ID;
import static bookstore.WebIntegrationTest.NONEXISTING_TITLE;
import static bookstore.WebIntegrationTest.PARENTHESIZE_TITLE_TEMPLATE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BookNotFoundExceptionTest {

    @Test
    public void testId() {
        Exception e = new BookNotFoundException(NONEXISTING_ID);
        assertEquals(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_ID) + NONEXISTING_ID, e.getMessage());
    }

    @Test
    public void testName() {
        Exception e = new BookNotFoundException(NONEXISTING_TITLE);
        assertEquals(String.format(BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, FIELD_TITLE)
                + String.format(PARENTHESIZE_TITLE_TEMPLATE, NONEXISTING_TITLE), e.getMessage());
    }

}
