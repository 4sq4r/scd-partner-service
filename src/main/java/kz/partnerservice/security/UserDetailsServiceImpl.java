package kz.partnerservice.security;

import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = repository.findByUsernameIgnoreCase(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username)
        );

        return new UserDetailsImpl(user);
    }
}
