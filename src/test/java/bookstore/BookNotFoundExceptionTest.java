package bookstore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BookNotFoundExceptionTest {

    @Test
    public void testId() {
        Exception e = new BookNotFoundException(WebIT.NONEXISTING_ID);
        assertEquals(String.format(WebIT.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIT.FIELD_ID) + WebIT.NONEXISTING_ID, e.getMessage());
    }

    @Test
    public void testName() {
        Exception e = new BookNotFoundException(WebIT.NONEXISTING_TITLE);
        assertEquals(String.format(WebIT.BOOK_NOT_FOUND_EXCEPTION_MESSAGE_TEMPLATE, WebIT.FIELD_TITLE)
                + String.format(WebIT.PARENTHESIZE_TITLE_TEMPLATE, WebIT.NONEXISTING_TITLE), e.getMessage());
    }

}
