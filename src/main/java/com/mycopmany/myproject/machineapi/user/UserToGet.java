package com.mycopmany.myproject.machineapi.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class UserToGet {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;

    public static UserToGet fromModel(User user) {
        return new UserToGet(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername()
        );
    }
}
