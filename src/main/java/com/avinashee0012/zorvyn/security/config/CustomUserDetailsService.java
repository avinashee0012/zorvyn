package com.avinashee0012.zorvyn.security.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.avinashee0012.zorvyn.domain.entity.User;
import com.avinashee0012.zorvyn.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + username)
            );

        return new CustomUserDetails(user);
    }
}
