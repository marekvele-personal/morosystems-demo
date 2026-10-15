package cz.marekvele.personal.morosystemsdemo.persistence.repository;

import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

}
