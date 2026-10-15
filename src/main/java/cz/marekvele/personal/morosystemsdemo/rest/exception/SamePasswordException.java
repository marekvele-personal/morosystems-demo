package cz.marekvele.personal.morosystemsdemo.rest.exception;

import org.springframework.http.HttpStatus;

public class SamePasswordException extends CommonException {

    public SamePasswordException() {
        super(HttpStatus.BAD_REQUEST, new Object[0]);
    }
}
