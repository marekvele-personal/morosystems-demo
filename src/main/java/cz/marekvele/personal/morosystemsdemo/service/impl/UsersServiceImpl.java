package cz.marekvele.personal.morosystemsdemo.service.impl;

import cz.marekvele.personal.morosystemsdemo.model.CreateUser;
import cz.marekvele.personal.morosystemsdemo.model.UpdateUser;
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
                .map(user -> new User(user.getId(), user.getName(), user.getUsername()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsers() {
        return usersRepository.findAll().stream()
                .map(user -> new User(user.getId(), user.getName(), user.getUsername()))
                .toList();
    }

    @Override
    public User createUser(CreateUser user) {
        //TODO check duplicate username
        UserEntity savedUser = usersRepository.save(new UserEntity(null, user.name(), user.username(), null));
        return new User(savedUser.getId(), savedUser.getName(), savedUser.getUsername());
    }

    @Override
    public User updateUser(Long id, UpdateUser user) {
        UserEntity userEntity = usersRepository.getReferenceById(id);
        userEntity.setName(user.name());
        UserEntity updatedUser = usersRepository.save(userEntity);
        return new User(updatedUser.getId(), updatedUser.getName(), updatedUser.getUsername());
    }

    @Override
    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }
}
