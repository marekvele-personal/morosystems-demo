package cz.marekvele.personal.morosystemsdemo.rest.controller;

import cz.marekvele.personal.morosystemsdemo.model.CreateOrUpdateUser;
import cz.marekvele.personal.morosystemsdemo.model.User;
import cz.marekvele.personal.morosystemsdemo.rest.exception.UserNotFoundException;
import cz.marekvele.personal.morosystemsdemo.service.UsersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable @Positive @NotNull Long id) {
        return usersService.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @GetMapping
    public List<User> getUsers() {
        return usersService.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid CreateOrUpdateUser user) {
        return usersService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable @Positive @NotNull Long id,
            @RequestBody CreateOrUpdateUser user
    ) {
        usersService.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return usersService.updateUser(new User(id, user.name()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive @NotNull Long id) {
        usersService.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        usersService.deleteUser(id);
    }
}
