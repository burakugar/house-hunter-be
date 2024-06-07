package com.house.hunter.security;

import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        final com.house.hunter.model.entity.User user =
                userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return new CustomUserDetails(user);
    }
}
