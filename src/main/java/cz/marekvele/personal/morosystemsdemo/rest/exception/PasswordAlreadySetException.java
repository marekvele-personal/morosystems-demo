package cz.marekvele.personal.morosystemsdemo.rest.exception;

import org.springframework.http.HttpStatus;

public class PasswordAlreadySetException extends CommonException {

    public PasswordAlreadySetException() {
        super(HttpStatus.UNAUTHORIZED, new Object[0]);
    }
}
