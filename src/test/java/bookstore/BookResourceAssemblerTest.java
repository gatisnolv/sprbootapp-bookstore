package bookstore;

import static bookstore.BookControllerTest.AGGREGATE_ROOT_REL_NAME;
import static bookstore.BookControllerTest.SELF_REL_NAME;
import static bookstore.BookControllerTest.SELF_REL_TEMPLATE;
import static bookstore.WebIntegrationTest.AGGREGATE_ROOT_INFIX;
import static bookstore.WebIntegrationTest.AUTHOR;
import static bookstore.WebIntegrationTest.PUBLICATION_DATE;
import static bookstore.WebIntegrationTest.PUBLISHER;
import static bookstore.WebIntegrationTest.TITLE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.hateoas.Resource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BookResourceAssemblerTest {

    private static final String ID_LINK_POSTFIX_PLACEHOLDER = "{id}";

    @Test
    public void test() {
        BookResourceAssembler assembler = new BookResourceAssembler();
        Book book = new Book(TITLE, AUTHOR, PUBLISHER, PUBLICATION_DATE);
        Resource<Book> resource = assembler.toResource(book);
        assertEquals(book, resource.getContent());
        assertEquals(String.format(SELF_REL_TEMPLATE, ID_LINK_POSTFIX_PLACEHOLDER), resource.getLink(SELF_REL_NAME).getHref());
        assertEquals(AGGREGATE_ROOT_INFIX, resource.getLink(AGGREGATE_ROOT_REL_NAME).getHref());
    }

}
