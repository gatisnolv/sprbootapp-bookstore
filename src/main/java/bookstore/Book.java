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

    public void setPublicationDate(String publicationDate){//TODO address incorrect date exception
        this.publicationDate=LocalDate.parse(publicationDate);
    }

    public String getPublicationDate() {
        return publicationDate.toString();
    }

}
