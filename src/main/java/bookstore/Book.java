package bookstore;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
class Book {

    private static final String EMPTY_STRING = "";

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private boolean publicationDateIsEmptyString;

    Book(String title, String author, String publisher, String publicationDate) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        setPublicationDate(publicationDate);
    }

    Book(Book book) {
        this.id = new Long(book.id);
        this.title = book.title;
        this.author = book.author;
        this.publisher = book.publisher;
        this.publicationDate = book.publicationDate;
        this.publicationDateIsEmptyString = book.publicationDateIsEmptyString;
    }

    public void setPublicationDate(String publicationDate) {//TODO q: how can I address incorrect date (format or impossible date) exception
        if (publicationDate == null) {
            publicationDateIsEmptyString = false;
            this.publicationDate = null;
            return;
        } else if (publicationDate.equals(EMPTY_STRING)) {
            publicationDateIsEmptyString = true;
            return;
        }
        publicationDateIsEmptyString = false;
        this.publicationDate = LocalDate.parse(publicationDate);
    }

    public String getPublicationDate() {
        if (publicationDateIsEmptyString) {
            return EMPTY_STRING;
        } else if (publicationDate == null) {
            return null;
        }
        return publicationDate.toString();
    }

    public boolean allFieldsEmpty() {
        return ((this.title == null || this.title.equals(EMPTY_STRING))
                && (this.author == null || this.author.equals(EMPTY_STRING))
                && (this.publisher == null || this.publisher.equals(EMPTY_STRING))
                && (this.publicationDate == null || isPublicationDateIsEmptyString()));
    }

}
