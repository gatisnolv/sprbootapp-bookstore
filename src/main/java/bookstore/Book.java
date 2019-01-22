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
    private String name;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private boolean publicationDateIsEmptyString;

    //try without this constructor to see whether the no-args constructor is then necessary
    Book(String name, String author, String publisher, String publicationDate) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        setPublicationDate(publicationDate);
    }

    public void setPublicationDate(String publicationDate) {//TODO how can I address incorrect date exception
        if (publicationDate == null) {//fix 6.1.2, but I would rather give an error if all replaced fields ar null
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
        if (publicationDateIsEmptyString) {//fix 6.1.2
            return EMPTY_STRING;
        } else if (publicationDate == null) {
            return null;
        }
        return publicationDate.toString();
    }

    public boolean allFieldsEmpty() {
        if ((this.name == null || this.name.equals(EMPTY_STRING))
                && (this.author == null || this.author.equals(EMPTY_STRING))
                && (this.publisher == null || this.publisher.equals(EMPTY_STRING))
                && (this.publicationDate == null || isPublicationDateIsEmptyString())) {
            return true;
        }
        return false;
    }

}
