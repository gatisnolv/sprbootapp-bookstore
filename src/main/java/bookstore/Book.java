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

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String author;
    private String publisher;
    private LocalDate publicationDate;

    //try without this constructor to see wheter the no-argsconstructor is then necessary
    Book(String name, String author, String publisher, String publicationDate) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        setPublicationDate(publicationDate);
    }

    public void setPublicationDate(String publicationDate) {//TODO how can I address incorrect date exception
        if (publicationDate == null) {//fix 6.1.2, but I would rather give an error if all replaced fields ar null
            this.publicationDate = null;
            return;
        }
        this.publicationDate = LocalDate.parse(publicationDate);
    }

    public String getPublicationDate() {
        if (publicationDate == null) {//fix 6.1.2
            return null;
        }
        return publicationDate.toString();
    }


    public void setName(String name) {
        this.name = name;
    }
}
