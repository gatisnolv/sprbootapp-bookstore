package bookstore;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class AllFieldsEmptyAdvice {
    @ResponseBody
    @ExceptionHandler(AllFieldsEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String allFieldsEmptyHandler(AllFieldsEmptyException x) {
        return x.getMessage();
    }

}
