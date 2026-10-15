package cz.marekvele.personal.morosystemsdemo.service.impl;

import cz.marekvele.personal.morosystemsdemo.model.*;
import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import cz.marekvele.personal.morosystemsdemo.persistence.repository.UsersRepository;
import cz.marekvele.personal.morosystemsdemo.rest.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void returnsUserDtoWhenUserExists() {
        Long id = 100L;
        UserEntity user = new UserEntity(id, "Test User", "testuser", null);
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = usersService.getUser(id);

        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(new User(id, "Test User", "testuser"));
        verify(usersRepository).findById(id);
    }

    @Test
    void returnsEmptyWhenUserDoesNotExist() {
        Long id = 100L;
        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = usersService.getUser(id);

        assertThat(result).isEmpty();
        verify(usersRepository).findById(id);
    }

    @Test
    void returnsAllUsersAsDtos() {
        when(usersRepository.findAll()).thenReturn(List.of(
                new UserEntity(1L, "První uživatel", "firstuser"),
                new UserEntity(2L, "Druhý uživatel", "seconduser")
        ));

        List<User> result = usersService.getUsers();

        assertThat(result).containsExactly(
                new User(1L, "První uživatel", "firstuser"),
                new User(2L, "Druhý uživatel", "seconduser")
        );
        verify(usersRepository).findAll();
    }

    @Test
    void createsUserAndReturnsSavedDto() {
        CreateUser input = new CreateUser("Nový uživatel", "testuser");
        UserEntity userToSave = new UserEntity(null, "Nový uživatel", "testuser");
        when(usersRepository.save(userToSave))
                .thenReturn(new UserEntity(3L, "Nový uživatel", "testuser"));

        User result = usersService.createUser(input);

        assertThat(result).isEqualTo(new User(3L, "Nový uživatel", "testuser"));
        verify(usersRepository).save(userToSave);
    }

    @Test
    void createUserWithDuplicateUsernameError() {
    CreateUser duplicate = new CreateUser("Nový uživatel", "testuser");
        when(usersRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(new UserEntity(4L, "Stávající uživatel", "testuser")));

        assertThatThrownBy(() -> usersService.createUser(duplicate))
                .isInstanceOf(DuplicateUsernameException.class);

        verify(usersRepository, never()).save(any());
    }

    @Test
    void updatesUserAndReturnsSavedDto() {
        Long id = 4L;
        UserEntity user = new UserEntity(id, "Aktualizovaný uživatel", "testuser");
        when(usersRepository.getReferenceById(id)).thenReturn(user);

        UpdateUser input = new UpdateUser("Aktualizovaný uživatel");

        when(usersRepository.save(user)).thenReturn(user);

        User result = usersService.updateUser(id, input);

        assertThat(result).isEqualTo(new User(id, "Aktualizovaný uživatel", "testuser"));
        verify(usersRepository).save(user);
    }

    @Test
    void deletesUserById() {
        Long id = 5L;
        usersService.deleteUser(id);

        verify(usersRepository).deleteById(id);
    }

    @Test
    void createsPasswordWhenUserHasNoPassword() {
        Long id = 6L;
        String encodedPassword = "encoded-new-password";
        UserEntity user = new UserEntity(id, "Test User", "testuser");
        SetPassword input = new SetPassword("new-password");
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn(encodedPassword);

        usersService.createPassword(id, input);

        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        verify(passwordEncoder).encode("new-password");
        verify(usersRepository).save(user);
    }

    @Test
    void createPasswordFailsWhenUserDoesNotExist() {
        Long id = 6L;
        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.createPassword(id, new SetPassword("new-password")))
                .isInstanceOf(UserNotFoundException.class);

        verify(usersRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createPasswordFailsWhenPasswordIsAlreadySet() {
        Long id = 6L;
        UserEntity user = new UserEntity(id, "Test User", "testuser", "encoded-current");
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> usersService.createPassword(id, new SetPassword("new-password")))
                .isInstanceOf(PasswordAlreadySetException.class);

        verify(usersRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changesPasswordWhenCurrentPasswordIsValidAndNewPasswordDiffers() {
        Long id = 7L;
        UserEntity user = new UserEntity(id, "Test User", "testuser", "encoded-current");
        ChangePassword input = new ChangePassword("current-password", "new-password");
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current-password", "encoded-current")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "encoded-current")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");

        usersService.changePassword(id, input);

        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(passwordEncoder).encode("new-password");
        verify(usersRepository).save(user);
    }

    @Test
    void changePasswordFailsWhenUserDoesNotExist() {
        Long id = 7L;
        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.changePassword(
                id, new ChangePassword("current-password", "new-password")))
                .isInstanceOf(UserNotFoundException.class);

        verify(usersRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changePasswordFailsWhenUserHasNoPassword() {
        Long id = 7L;
        UserEntity user = new UserEntity(id, "Test User", "testuser", null);
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> usersService.changePassword(
                id, new ChangePassword("current-password", "new-password")))
                .isInstanceOf(InvalidCurrentPasswordException.class);

        verify(usersRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changePasswordFailsWhenCurrentPasswordDoesNotMatch() {
        Long id = 7L;
        UserEntity user = new UserEntity(id, "Test User", "testuser", "encoded-current");
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-current")).thenReturn(false);

        assertThatThrownBy(() -> usersService.changePassword(
                id, new ChangePassword("wrong-password", "new-password")))
                .isInstanceOf(InvalidCurrentPasswordException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(usersRepository, never()).save(any());
    }

    @Test
    void changePasswordFailsWhenNewPasswordIsSameAsCurrentPassword() {
        Long id = 7L;
        UserEntity user = new UserEntity(id, "Test User", "testuser", "encoded-current");
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current-password", "encoded-current")).thenReturn(true);
        when(passwordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> usersService.changePassword(
                id, new ChangePassword("current-password", "current-password")))
                .isInstanceOf(SamePasswordException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(usersRepository, never()).save(any());
    }
}
