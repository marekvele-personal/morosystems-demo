package cz.marekvele.personal.morosystemsdemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPassword(@NotBlank @Size(min = 8) String newPassword) {
}
