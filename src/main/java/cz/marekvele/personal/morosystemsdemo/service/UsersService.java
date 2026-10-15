package cz.marekvele.personal.morosystemsdemo.service;

import cz.marekvele.personal.morosystemsdemo.model.User;

import java.util.Optional;

public interface UsersService {

    Optional<User> getUser(Long id);

}
