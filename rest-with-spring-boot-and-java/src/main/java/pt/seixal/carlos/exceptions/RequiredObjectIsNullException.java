package pt.seixal.carlos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RequiredObjectIsNullException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public RequiredObjectIsNullException() {
        super("It is not allowed to pass null as a required object");
    }

    public RequiredObjectIsNullException(String message) {
        super(message);
    }
}
