package cz.marekvele.personal.morosystemsdemo.persistence.repository;

import cz.marekvele.personal.morosystemsdemo.constants.Profiles;
import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles(Profiles.TEST)
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void savesFindsUpdatesAndDeletesUser() {
        UserEntity user = new UserEntity(100L, "Test User", "testuser");

        UserEntity savedUser = usersRepository.saveAndFlush(user);

        assertThat(usersRepository.findById(savedUser.getId()))
                .isPresent()
                .get()
                .extracting(UserEntity::getName)
                .isEqualTo("Test User");

        savedUser.setName("Updated User");
        usersRepository.saveAndFlush(savedUser);

        assertThat(usersRepository.findById(savedUser.getId()))
                .isPresent()
                .get()
                .extracting(UserEntity::getName)
                .isEqualTo("Updated User");

        usersRepository.delete(savedUser);
        usersRepository.flush();

        assertThat(usersRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    void returnsSavedUsersInFindAll() {
        UserEntity firstUser = usersRepository.save(new UserEntity(101L, "First User", "firstuser"));
        UserEntity secondUser = usersRepository.save(new UserEntity(102L, "Second User","seconduser"));
        usersRepository.flush();

        List<UserEntity> users = usersRepository.findAll();

        assertThat(users)
                .extracting(UserEntity::getId)
                .contains(firstUser.getId(), secondUser.getId());
    }

    @Test
    void findsUserByUsername() {
        UserEntity user = usersRepository.saveAndFlush(
                new UserEntity(103L, "Test User", "testuser"));

        assertThat(usersRepository.findByUsername("testuser"))
                .contains(user);
        assertThat(usersRepository.findByUsername("unknown")).isEmpty();
    }
}
