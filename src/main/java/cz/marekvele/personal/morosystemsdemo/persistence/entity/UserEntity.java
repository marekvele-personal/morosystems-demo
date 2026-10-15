package cz.marekvele.personal.morosystemsdemo.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString( exclude = "password")
public class UserEntity {

    public UserEntity(String name, String username) {
        this.name = name;
        this.username = username;
    }

    public UserEntity(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    @Id
    @GeneratedValue(generator = "users_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "users_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "username", nullable = false, length = 256)
    private String username;

    @Column(name = "password", length = 256)
    private String password;

}
