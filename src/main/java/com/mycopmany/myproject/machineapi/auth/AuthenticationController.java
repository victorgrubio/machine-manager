package com.mycopmany.myproject.machineapi.auth;

import com.mycopmany.myproject.machineapi.user.UserToCreate;
import com.mycopmany.myproject.machineapi.user.UserToLogin;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserToCreate userToCreate){
        return ResponseEntity.ok(authenticationService.register(userToCreate));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody UserToLogin userToLogin){
        return ResponseEntity.ok(authenticationService.authenticate(userToLogin));
    }

}
