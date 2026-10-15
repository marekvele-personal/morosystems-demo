package cz.marekvele.personal.morosystemsdemo.service.impl;

import cz.marekvele.personal.morosystemsdemo.model.CreateUser;
import cz.marekvele.personal.morosystemsdemo.model.UpdateUser;
import cz.marekvele.personal.morosystemsdemo.model.User;
import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import cz.marekvele.personal.morosystemsdemo.persistence.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

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
                new UserEntity(1L, "První uživatel", "firstuser", null),
                new UserEntity(2L, "Druhý uživatel", "seconduser", null)
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
        UserEntity userToSave = new UserEntity(null, "Nový uživatel", "testuser", null);
        when(usersRepository.save(userToSave))
                .thenReturn(new UserEntity(3L, "Nový uživatel", "testuser", null));

        User result = usersService.createUser(input);

        assertThat(result).isEqualTo(new User(3L, "Nový uživatel", "testuser"));
        verify(usersRepository).save(userToSave);
    }

    @Test
    void updatesUserAndReturnsSavedDto() {
        Long id = 4L;
        UserEntity user = new UserEntity(id, "Aktualizovaný uživatel", "testuser", null);
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
}
