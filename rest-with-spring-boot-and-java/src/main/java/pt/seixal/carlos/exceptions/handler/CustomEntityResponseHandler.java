package pt.seixal.carlos.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pt.seixal.carlos.exceptions.ExceptionResponse;
import pt.seixal.carlos.exceptions.FileNotFoundException;
import pt.seixal.carlos.exceptions.FileStorageException;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomEntityResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(getResponse(ex, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(getResponse(ex, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequiredObjectIsNullException.class)
    public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(getResponse(ex, request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleFileNotFoundExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(getResponse(ex, request), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(FileStorageException.class)
    public final ResponseEntity<ExceptionResponse> handleFileStoreExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(getResponse(ex, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public final ExceptionResponse getResponse(Exception ex, WebRequest request) {
        return  new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
    }
}
