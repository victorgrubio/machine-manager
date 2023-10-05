package com.mycopmany.myproject.machineapi.config;

import com.mycopmany.myproject.machineapi.user.Role;
import com.mycopmany.myproject.machineapi.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceTest {
    @Mock
    private UserDetails userDetails;
    private JwtService jwtService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
    }

    @Test
    void generateTokenAndExtractUsername() {
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);

        String token = jwtService.generateToken(user);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(user.getUsername(),extractedUsername);
    }



    @Test
    void isTokenValid() {
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);
        when(userDetails.getUsername()).thenReturn(user.getUsername());

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenInvalid() {
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);
        when(userDetails.getUsername()).thenReturn(user.getUsername());

        String expiredToken = jwtService.generateTokenWithCustomExpiration(new HashMap<>(),
                user,new Date(System.currentTimeMillis() + 1));
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, userDetails));
    }


}