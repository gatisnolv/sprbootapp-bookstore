package bookstore;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class BookResourceAssembler implements ResourceAssembler<Book, Resource<Book>> {

    @Override
    public Resource<Book> toResource(Book book) {
        return new Resource<>(book,
                linkTo(methodOn(BookController.class).getOneBookById(book.getId())).withSelfRel(),
                linkTo(methodOn(BookController.class).getAllBooks()).withRel("books"));
    }

}
