package hexlet.code.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("User " + id + " not found");
    }

    public UserNotFoundException(String email) {
        super("User " + email + " not found");
    }

}
