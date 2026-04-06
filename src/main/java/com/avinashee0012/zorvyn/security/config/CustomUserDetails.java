package com.avinashee0012.zorvyn.security.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.avinashee0012.zorvyn.domain.entity.User;

public class CustomUserDetails implements UserDetails {
    private final User user;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public User getUser() {
        return user;
    }
}
