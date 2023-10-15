package com.mycopmany.myproject.machineapi.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/users")
@Tag(name = "User-Controller", description = "Admin-only controller")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserToGet> getUsers(){
        return userService.getUsers();
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long Id){userService.deleteUser(Id);}


}
