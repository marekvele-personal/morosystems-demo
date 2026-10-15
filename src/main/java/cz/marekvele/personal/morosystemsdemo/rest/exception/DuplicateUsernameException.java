package cz.marekvele.personal.morosystemsdemo.rest.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUsernameException extends CommonException {

    public DuplicateUsernameException(String username) {
        super(HttpStatus.CONFLICT, new String[] {username});
    }
}
