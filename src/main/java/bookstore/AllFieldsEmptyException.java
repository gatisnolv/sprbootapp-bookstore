package bookstore;

public class AllFieldsEmptyException extends RuntimeException {

    AllFieldsEmptyException() {
        super("All fields were missing/empty, the entry was not saved");
    }

}
