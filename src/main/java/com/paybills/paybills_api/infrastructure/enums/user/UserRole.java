package com.paybills.paybills_api.infrastructure.enums.user;

import lombok.Getter;

@Getter
public enum UserRole {

    USER("user");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

}
