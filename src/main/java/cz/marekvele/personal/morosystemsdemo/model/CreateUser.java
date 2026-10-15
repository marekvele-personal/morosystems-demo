package cz.marekvele.personal.morosystemsdemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUser(@NotBlank @Size(max=256) String name, @NotBlank @Size(max=256) String username) {
}
