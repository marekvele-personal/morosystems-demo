package cz.marekvele.personal.morosystemsdemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePassword(
        @NotBlank @Size(min = 8) String currentPassword,
        @NotBlank @Size(min = 8) String newPassword
) {
}
