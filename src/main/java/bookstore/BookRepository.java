package bookstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select book from Book book where LOWER(book.name) = LOWER(:name)")
    Book findByName(@Param("name") String name);
}
