package bookstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select book from Book book where LOWER(book.title) = LOWER(:title)")
    Book findByTitle(@Param("title") String title);
}
