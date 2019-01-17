package bookstore;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
