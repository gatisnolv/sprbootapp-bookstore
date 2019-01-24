package bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.hateoas.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class BookResourceAssemblerTest {

    @Test
    public void test() {
        BookResourceAssembler assembler = new BookResourceAssembler();
        String selfLink = "/books/{id}";
        String aggregateRootLink = "/books";
        Book book = new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08");
        Resource<Book> resource = assembler.toResource(book);
        assertEquals(book, resource.getContent());
        assertEquals(selfLink, resource.getLink("self").getHref());
        assertEquals(aggregateRootLink, resource.getLink("books").getHref());
    }

}
