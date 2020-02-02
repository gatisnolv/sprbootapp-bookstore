package bookstore;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookDTO {
    private String title;
    private String author;
    private String publisher;
    private String publicationDate;
}
