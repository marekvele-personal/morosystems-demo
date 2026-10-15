package cz.marekvele.personal.morosystemsdemo.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString( exclude = "password")
public class UserEntity {

    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "username", nullable = false, length = 256)
    private String username;

    @Column(name = "password", length = 256)
    private String password;

}
