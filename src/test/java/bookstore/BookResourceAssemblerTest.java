package bookstore;

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
        Book book = new Book(WebIT.TITLE, WebIT.AUTHOR, WebIT.PUBLISHER, WebIT.PUBLICATION_DATE);
        Resource<Book> resource = assembler.toResource(book);
        assertEquals(book, resource.getContent());
        assertEquals(String.format(BookControllerTest.SELF_REL_TEMPLATE, ID_LINK_POSTFIX_PLACEHOLDER),
                resource.getLink(BookControllerTest.SELF_REL_NAME).getHref());
        assertEquals(WebIT.AGGREGATE_ROOT_INFIX, resource.getLink(BookControllerTest.AGGREGATE_ROOT_REL_NAME).getHref());
    }

}
