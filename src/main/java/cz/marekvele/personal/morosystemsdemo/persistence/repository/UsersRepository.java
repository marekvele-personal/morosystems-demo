package cz.marekvele.personal.morosystemsdemo.persistence.repository;

import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {
}
