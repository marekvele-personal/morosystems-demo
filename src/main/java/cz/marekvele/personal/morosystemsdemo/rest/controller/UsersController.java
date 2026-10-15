package cz.marekvele.personal.morosystemsdemo.rest.controller;

import cz.marekvele.personal.morosystemsdemo.model.CreateUser;
import cz.marekvele.personal.morosystemsdemo.model.UpdateUser;
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
    public User createUser(@RequestBody @Valid CreateUser user) {
        return usersService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable @Positive @NotNull Long id,
            @RequestBody UpdateUser user
    ) {
        usersService.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return usersService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive @NotNull Long id) {
        usersService.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        usersService.deleteUser(id);
    }
}
