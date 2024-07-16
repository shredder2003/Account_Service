package account.exception;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

@ControllerAdvice
public class ResponseEntityExceptionHandlerImpl extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String url = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toString();
        account.exception.ErrorResponse errorResponse = new account.exception.ErrorResponse.ErrorResponseBuilder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error( HttpStatus.BAD_REQUEST.getReasonPhrase() )
                .message(Arrays.toString(ex.getDetailMessageArguments()))
                .path(url.substring(StringUtils.ordinalIndexOf(url, "/", 3)) )
                .build();
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String url = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toString();
        account.exception.ErrorResponse errorResponse = new account.exception.ErrorResponse.ErrorResponseBuilder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(url.substring(StringUtils.ordinalIndexOf(url, "/", 3)) )
                .build();
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            //HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String url = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toString();
        account.exception.ErrorResponse errorResponse = new account.exception.ErrorResponse.ErrorResponseBuilder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(url.substring(StringUtils.ordinalIndexOf(url, "/", 3)) )
                .build();
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(
            ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        /*Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.error("getDetailMessageCode="+ex.getDetailMessageCode()
                +" getBody="+ex.getBody().getDetail()
        );*/
        account.exception.ErrorResponse errorResponse = new account.exception.ErrorResponse.ErrorResponseBuilder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getBody().getDetail()/*ex.getDetailMessageArguments()==null?ex.getMessage():ex.getDetailMessageArguments().toString()*/)
                .path(ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toString() )
                .build();
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(errorResponse);
}


    // BasicErrorController will handle the exception. Just overriding status code
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
