package cz.marekvele.personal.morosystemsdemo.security.service;

import cz.marekvele.personal.morosystemsdemo.persistence.entity.UserEntity;
import cz.marekvele.personal.morosystemsdemo.persistence.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static cz.marekvele.personal.morosystemsdemo.constants.Roles.ROLE_USER;

@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(ROLE_USER)
                .build();
    }
}
