package cz.marekvele.personal.morosystemsdemo.service.impl;

import cz.marekvele.personal.morosystemsdemo.model.CreateOrUpdateUser;
import cz.marekvele.personal.morosystemsdemo.model.User;
import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import cz.marekvele.personal.morosystemsdemo.persistence.repository.UsersRepository;
import cz.marekvele.personal.morosystemsdemo.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUser(Long id) {
        return usersRepository.findById(id)
                .map(user -> new User(user.getId(), user.getName()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsers() {
        return usersRepository.findAll().stream()
                .map(user -> new User(user.getId(), user.getName()))
                .toList();
    }

    @Override
    public User createUser(CreateOrUpdateUser user) {
        UserEntity savedUser = usersRepository.save(new UserEntity(null, user.name()));
        return new User(savedUser.getId(), savedUser.getName());
    }

    @Override
    public User updateUser(User user) {
        UserEntity updatedUser = usersRepository.save(new UserEntity(user.id(), user.name()));
        return new User(updatedUser.getId(), updatedUser.getName());
    }

    @Override
    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }
}
