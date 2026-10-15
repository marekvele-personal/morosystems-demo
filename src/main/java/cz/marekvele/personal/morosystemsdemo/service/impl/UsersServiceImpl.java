package cz.marekvele.personal.morosystemsdemo.service.impl;

import cz.marekvele.personal.morosystemsdemo.model.*;
import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import cz.marekvele.personal.morosystemsdemo.persistence.repository.UsersRepository;
import cz.marekvele.personal.morosystemsdemo.rest.exception.*;
import cz.marekvele.personal.morosystemsdemo.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUser(Long id) {
        log.info("Getting user: {}", id);

        return usersRepository.findById(id)
                .map(user -> new User(user.getId(), user.getName(), user.getUsername()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsers() {
        log.info("Retrieving all users");

        return usersRepository.findAll().stream()
                .map(user -> new User(user.getId(), user.getName(), user.getUsername()))
                .toList();
    }

    @Override
    public User createUser(CreateUser user) {
        if (usersRepository.findByUsername(user.username()).isPresent()) {
            throw new DuplicateUsernameException(user.username());
        }

        log.info("Creating new user: {}", user);

        UserEntity savedUser = usersRepository.save(new UserEntity(null, user.name(), user.username(), null));
        return new User(savedUser.getId(), savedUser.getName(), savedUser.getUsername());
    }

    @Override
    public User updateUser(Long id, UpdateUser user) {
        UserEntity userEntity = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        log.info("Update user: {} with data: {}", id, user);

        userEntity.setName(user.name());
        UserEntity updatedUser = usersRepository.save(userEntity);
        return new User(updatedUser.getId(), updatedUser.getName(), updatedUser.getUsername());
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);

        usersRepository.deleteById(id);
    }

    @Override
    public void createPassword(Long id, SetPassword password) {
        log.info("Attempting to set first password for user: {}", id);

        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            throw new PasswordAlreadySetException();
        }

        log.info("First password set for user: {}", id);

        user.setPassword(passwordEncoder.encode(password.newPassword()));
        usersRepository.save(user);
    }

    @Override
    public void changePassword(Long id, ChangePassword password) {
        log.info("Password change attempt for user: {}", id);

        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !Objects.equals(user.getUsername(), authentication.getName())) {
            throw new AccessDeniedException("Access is denied");
        }

        if (user.getPassword() == null
                || !passwordEncoder.matches(password.currentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }
        if (passwordEncoder.matches(password.newPassword(), user.getPassword())) {
            throw new SamePasswordException();
        }

        log.info("Password changed for user: {}", id);

        user.setPassword(passwordEncoder.encode(password.newPassword()));
        usersRepository.save(user);
    }
}
