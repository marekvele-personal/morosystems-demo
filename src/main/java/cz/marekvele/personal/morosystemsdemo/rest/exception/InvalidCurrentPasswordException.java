package cz.marekvele.personal.morosystemsdemo.rest.exception;

import org.springframework.http.HttpStatus;

public class InvalidCurrentPasswordException extends CommonException {

    public InvalidCurrentPasswordException() {
        super(HttpStatus.UNAUTHORIZED, new Object[0]);
    }
}
