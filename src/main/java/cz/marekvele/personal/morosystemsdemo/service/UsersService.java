package cz.marekvele.personal.morosystemsdemo.service;

import cz.marekvele.personal.morosystemsdemo.model.CreateUser;
import cz.marekvele.personal.morosystemsdemo.model.UpdateUser;
import cz.marekvele.personal.morosystemsdemo.model.User;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    Optional<User> getUser(Long id);

    List<User> getUsers();

    User createUser(CreateUser user);

    User updateUser(Long id, UpdateUser user);

    void deleteUser(Long id);

}
