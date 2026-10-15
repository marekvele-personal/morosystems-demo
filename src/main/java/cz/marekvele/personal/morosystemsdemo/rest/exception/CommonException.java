package cz.marekvele.personal.morosystemsdemo.rest.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public abstract class CommonException extends RuntimeException {

    protected final HttpStatus status;
    protected final Object[] args;

}
