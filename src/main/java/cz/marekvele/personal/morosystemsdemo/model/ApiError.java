package cz.marekvele.personal.morosystemsdemo.model;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        List<String> messages,
        String path
) {
}
