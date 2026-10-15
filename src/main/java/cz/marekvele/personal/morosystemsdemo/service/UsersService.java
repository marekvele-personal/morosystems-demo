package cz.marekvele.personal.morosystemsdemo.service;

import cz.marekvele.personal.morosystemsdemo.model.CreateOrUpdateUser;
import cz.marekvele.personal.morosystemsdemo.model.User;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    Optional<User> getUser(Long id);

    List<User> getUsers();

    User createUser(CreateOrUpdateUser user);

    User updateUser(User user);

    void deleteUser(Long id);

}
