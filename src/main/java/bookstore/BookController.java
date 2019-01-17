package bookstore;

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
        if (book==null){
            throw new BookNotFoundException(name);
        }
        return assembler.toResource(book);
    }


}
