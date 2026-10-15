package cz.marekvele.personal.morosystemsdemo.service;

import cz.marekvele.personal.morosystemsdemo.model.*;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    Optional<User> getUser(Long id);

    List<User> getUsers();

    User createUser(CreateUser user);

    User updateUser(Long id, UpdateUser user);

    void deleteUser(Long id);

    void createPassword(Long id, SetPassword password);

    void changePassword(Long id, ChangePassword password);

}
