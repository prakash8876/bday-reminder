package io.matoshri.bdayreminder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DateParseException extends ApiException {
    public DateParseException(String message) {
        super(message);
    }

    public DateParseException() {
    }

    public DateParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
