package com.mycopmany.myproject.machineapi.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserToCreate {
    private String firstName;
    private String lastName;
    private String username;
    private String password;


}
