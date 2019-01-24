package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class BookNotFoundAdviceTest {

    @Test
    public void test() {
        BookNotFoundAdvice advice = new BookNotFoundAdvice();
        Long id = 7L;
        String message = "Could not find book with id: " + id;
        BookNotFoundException e = new BookNotFoundException(id);
        assertEquals(message, advice.bookNotFoundHandler(e));
    }

}
