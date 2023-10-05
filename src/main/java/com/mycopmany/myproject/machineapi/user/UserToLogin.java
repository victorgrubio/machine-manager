package com.mycopmany.myproject.machineapi.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserToLogin {
    private String username;
    private String password;
}
