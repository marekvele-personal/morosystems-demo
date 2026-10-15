package cz.marekvele.personal.morosystemsdemo.rest.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CommonException {

    public UserNotFoundException(long id) {
        super(HttpStatus.NOT_FOUND, new String[] {String.valueOf(id)});
    }
}
