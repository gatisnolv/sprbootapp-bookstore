package bookstore;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
class BookController {

    private static final String EMPTY_STRING = "";

    private final BookRepository repository;
    private final BookResourceAssembler assembler;

    BookController(BookRepository repository, BookResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //Aggregate root
    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    Resources<Resource<Book>> all() {
        List<Resource<Book>> books = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(books, linkTo(methodOn(BookController.class).all()).withSelfRel());
    }

    @PostMapping("/books")
    ResponseEntity<?> newBook(@RequestBody Book newBook) throws URISyntaxException {
        if (allFieldsEmpty(newBook)) {
            throw new AllFieldsEmptyException();
        }
        Resource<Book> resource = assembler.toResource(repository.save(newBook));
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    //Single item
    @GetMapping("/books/{id}")
    Resource<Book> oneById(@PathVariable Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));//TODO create custom exception
        return assembler.toResource(book);
    }

    @GetMapping("/books/name/{name}")
    Resource<Book> oneByName(@PathVariable String name) {
        Book book = repository.findByName(name);
        if (book == null) {
            throw new BookNotFoundException(name);
        }
        return assembler.toResource(book);
    }

    @PutMapping("/books/{id}")
    ResponseEntity<?> replaceBook(@RequestBody Book newBook, @PathVariable Long id) throws URISyntaxException {
        //TODO check if new book is not only null or empty string values
        if (allFieldsEmpty(newBook)) {
            throw new AllFieldsEmptyException();
        }
        Book updatedBook = repository.findById(id).map(book -> {
            book.setName(newBook.getName());
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
    ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new BookNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }

    private boolean allFieldsEmpty(Book book) {
        String name = book.getName();
        String author = book.getAuthor();
        String publisher = book.getPublisher();
        String publicationDate = book.getPublicationDate();
        if ((name == null || name.equals(EMPTY_STRING))
                && (author == null || author.equals(EMPTY_STRING))
                && (publisher == null || publisher.equals(EMPTY_STRING))
                && (publicationDate == null || publicationDate.equals(EMPTY_STRING))) {
            return true;
        }
        return false;
    }
}