package hexlet.code.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TaskStatusInUseException extends RuntimeException {
    public TaskStatusInUseException() {
        super("Невозможно удалить. Статус используется.");
    }
}
