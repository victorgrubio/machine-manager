package com.mycopmany.myproject.machineapi.auth;

import com.mycopmany.myproject.machineapi.user.UserToCreate;
import com.mycopmany.myproject.machineapi.user.UserToLogin;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@RequestBody UserToCreate userToCreate){
        authenticationService.register(userToCreate);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody UserToLogin userToLogin){
        return ResponseEntity.ok(authenticationService.authenticate(userToLogin));
    }

}
