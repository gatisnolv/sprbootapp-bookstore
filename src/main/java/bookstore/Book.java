package bookstore;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
class Book {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;

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
    }

    public void setPublicationDate(String publicationDate) {//TODO q: how can I address incorrect date (format or impossible date) exception
        if (publicationDate == null) {
            this.publicationDate = null;
            return;
        }
        this.publicationDate = LocalDate.parse(publicationDate);
    }

    public String getPublicationDate() {
        if (publicationDate == null) {
            return null;
        }
        return publicationDate.toString();
    }

    public boolean allFieldsNull() {
        return title == null && author == null && publisher == null && publicationDate == null;
    }

}
