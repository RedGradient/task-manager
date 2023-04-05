package hexlet.code.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LabelNotFoundException extends RuntimeException {
    public LabelNotFoundException(Long id) {
        super("Label with id " + id + " not found");
    }
}
