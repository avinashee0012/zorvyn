package com.avinashee0012.zorvyn.domain.entity;

import com.avinashee0012.zorvyn.domain.enums.Role;
import com.avinashee0012.zorvyn.domain.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.VIEWER;

    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    public User(String name, String email, String encryptedPassword) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }

        this.name = name.trim();
        this.email = email.trim();
        this.password = encryptedPassword;
    }

    public void updateProfile(String name, Role role, UserStatus status) {
        if (name != null) {
            this.name = name.trim();
        }

        if (role != null) {
            this.role = role;
        }

        if (status != null) {
            this.status = status;
        }
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    public void toggleStatus() {
        this.status = this.status == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
