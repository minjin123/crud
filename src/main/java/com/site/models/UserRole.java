package com.site.models;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");

    private String value;

    UserRole(String value) {
        this.value = value;
    }


}
