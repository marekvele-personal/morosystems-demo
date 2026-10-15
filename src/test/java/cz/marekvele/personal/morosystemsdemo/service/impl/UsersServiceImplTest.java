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
        when(usersRepository.save(new UserEntity(null, "Nový uživatel", "testuser", null)))
                .thenReturn(new UserEntity(3L, "Nový uživatel", "testuser", null));

        User result = usersService.createUser(input);

        assertThat(result).isEqualTo(new User(3L, "Nový uživatel", "testuser"));
        verify(usersRepository).save(new UserEntity(null, "Nový uživatel", "testuser", null));
    }

    @Test
    void updatesUserAndReturnsSavedDto() {
        when(usersRepository.getReferenceById(4L))
                .thenReturn(new UserEntity(4L, "Aktualizovaný uživatel", "testuser", null));

        UpdateUser input = new UpdateUser("Aktualizovaný uživatel");

        when(usersRepository.save(new UserEntity(4L, "Aktualizovaný uživatel", "testuser",null)))
                .thenReturn(new UserEntity(4L, "Aktualizovaný uživatel", "testuser", null));

        User result = usersService.updateUser(4L, input);

        assertThat(result).isEqualTo(new User(4L, "Aktualizovaný uživatel", "testuser"));
        verify(usersRepository).save(new UserEntity(4L, "Aktualizovaný uživatel", "testuser", null));
    }

    @Test
    void deletesUserById() {
        usersService.deleteUser(5L);

        verify(usersRepository).deleteById(5L);
    }
}
