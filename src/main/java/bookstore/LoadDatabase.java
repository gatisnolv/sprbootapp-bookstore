package bookstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(BookRepository repository) {
        return args -> {
            repository.save(new Book("1984", "George Orwell", "Secker & Warburg", "1949-06-08"));
            repository.save(new Book("To Kill a Mockingbird", "Harper Lee", "J. B. Lippincott & Co.", "1960-11-07"));
            repository.save(new Book("Animal Farm", "George Orwell", "Secker & Warburg", "1945-08-17"));

            repository.findAll().forEach(book ->
                    log.info("Preloading " + book)
            );
        };
    }
}
