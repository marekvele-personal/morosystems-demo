package cz.marekvele.personal.morosystemsdemo.service.impl;

import cz.marekvele.personal.morosystemsdemo.model.*;
import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import cz.marekvele.personal.morosystemsdemo.persistence.repository.UsersRepository;
import cz.marekvele.personal.morosystemsdemo.rest.exception.*;
import cz.marekvele.personal.morosystemsdemo.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

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
        if (usersRepository.findByUsername(user.username()).isPresent()) {
            throw new DuplicateUsernameException(user.username());
        }
        UserEntity savedUser = usersRepository.save(new UserEntity(null, user.name(), user.username(), null));
        return new User(savedUser.getId(), savedUser.getName(), savedUser.getUsername());
    }

    @Override
    public User updateUser(Long id, UpdateUser user) {
        UserEntity userEntity = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userEntity.setName(user.name());
        UserEntity updatedUser = usersRepository.save(userEntity);
        return new User(updatedUser.getId(), updatedUser.getName(), updatedUser.getUsername());
    }

    @Override
    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }

    @Override
    public void createPassword(Long id, SetPassword password) {
        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            throw new PasswordAlreadySetException();
        }

        user.setPassword(passwordEncoder.encode(password.newPassword()));
        usersRepository.save(user);
    }

    @Override
    public void changePassword(Long id, ChangePassword password) {
        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getPassword() == null
                || !passwordEncoder.matches(password.currentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }
        if (passwordEncoder.matches(password.newPassword(), user.getPassword())) {
            throw new SamePasswordException();
        }

        user.setPassword(passwordEncoder.encode(password.newPassword()));
        usersRepository.save(user);
    }
}
