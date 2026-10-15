package cz.marekvele.personal.morosystemsdemo.service.impl;

import cz.marekvele.personal.morosystemsdemo.model.CreateOrUpdateUser;
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
        UserEntity user = new UserEntity(id, "Test User");
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = usersService.getUser(id);

        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(new User(id, "Test User"));
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
                new UserEntity(1L, "První uživatel"),
                new UserEntity(2L, "Druhý uživatel")
        ));

        List<User> result = usersService.getUsers();

        assertThat(result).containsExactly(
                new User(1L, "První uživatel"),
                new User(2L, "Druhý uživatel")
        );
        verify(usersRepository).findAll();
    }

    @Test
    void createsUserAndReturnsSavedDto() {
        CreateOrUpdateUser input = new CreateOrUpdateUser("Nový uživatel");
        when(usersRepository.save(new UserEntity(null, "Nový uživatel")))
                .thenReturn(new UserEntity(3L, "Nový uživatel"));

        User result = usersService.createUser(input);

        assertThat(result).isEqualTo(new User(3L, "Nový uživatel"));
        verify(usersRepository).save(new UserEntity(null, "Nový uživatel"));
    }

    @Test
    void updatesUserAndReturnsSavedDto() {
        User input = new User(4L, "Aktualizovaný uživatel");
        when(usersRepository.save(new UserEntity(4L, "Aktualizovaný uživatel")))
                .thenReturn(new UserEntity(4L, "Aktualizovaný uživatel"));

        User result = usersService.updateUser(input);

        assertThat(result).isEqualTo(input);
        verify(usersRepository).save(new UserEntity(4L, "Aktualizovaný uživatel"));
    }

    @Test
    void deletesUserById() {
        usersService.deleteUser(5L);

        verify(usersRepository).deleteById(5L);
    }
}
