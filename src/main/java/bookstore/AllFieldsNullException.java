package bookstore;

class AllFieldsNullException extends RuntimeException {

    AllFieldsNullException() {
        super("All fields were missing/null, the entry was not saved");
    }

}
