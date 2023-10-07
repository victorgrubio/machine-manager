package com.mycopmany.myproject.machineapi.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    @GetMapping
    public List<UserToGet> getUsers(){
        return userService.getUsers();
    }

    @DeleteMapping(path = "{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long userId){userService.deleteUser(userId);}


}
