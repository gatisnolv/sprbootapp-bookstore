package bookstore;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
class BookController {

    private final BookRepository repository;
    private final BookResourceAssembler assembler;

    BookController(BookRepository repository, BookResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //Aggregate root
    @GetMapping(value = "/books", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
    Resources<Resource<Book>> getAllBooks() {
        List<Resource<Book>> books = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(books, linkTo(methodOn(BookController.class).getAllBooks()).withSelfRel());
    }

    @PostMapping("/books")
    ResponseEntity<?> postNewBook(@RequestBody Book newBook) throws URISyntaxException {
        if (newBook.allFieldsNull()) {
            throw new AllFieldsNullException();
        }
        Resource<Book> resource = assembler.toResource(repository.save(newBook));
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    //Single item
    @GetMapping(value = "/books/{id}", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
    Resource<Book> getOneBookById(@PathVariable Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return assembler.toResource(book);
    }

    @GetMapping(value = "/books/title/{title}", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
    Resource<Book> getOneBookByTitle(@PathVariable String title) {
        Book book = repository.findByTitleIgnoreCase(title);
        if (book == null) {
            throw new BookNotFoundException(title);
        }
        return assembler.toResource(book);
    }

    @PutMapping("/books/{id}")
    ResponseEntity<?> replaceBookById(@RequestBody Book newBook, @PathVariable Long id) throws URISyntaxException {
        if (newBook.allFieldsNull()) {
            throw new AllFieldsNullException();
        }
        Book updatedBook = repository.findById(id).map(book -> {
            book.setTitle(newBook.getTitle());
            book.setAuthor(newBook.getAuthor());
            book.setPublisher(newBook.getPublisher());
            book.setPublicationDate(newBook.getPublicationDate());
            return repository.save(book);
        }).orElseThrow(() -> new BookNotFoundException(id));

        Resource<?> resource = assembler.toResource(updatedBook);

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/books/{id}")
    ResponseEntity<?> deleteBookById(@PathVariable Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new BookNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }

}