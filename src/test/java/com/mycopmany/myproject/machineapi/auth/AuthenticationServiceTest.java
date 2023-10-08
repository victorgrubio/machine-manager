package com.mycopmany.myproject.machineapi.auth;

import com.mycopmany.myproject.machineapi.config.JwtService;
import com.mycopmany.myproject.machineapi.exception.ConflictException;
import com.mycopmany.myproject.machineapi.exception.UnauthorizedException;
import com.mycopmany.myproject.machineapi.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager);
    }

    @Test
    void registerValidUser() {
        UserToCreate userToCreate = new UserToCreate("firstname",
                "lastname",
                "username",
                "password");
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);

        when(userRepository.existsByUsername(userToCreate.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(userToCreate.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(user)).thenReturn("jwtToken");
        AuthenticationResponse response = authenticationService.register(userToCreate);
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(Role.USER, user.getRole());

        verify(userRepository, times(1)).save(user);
    }
    @Test
    void registerInvalidUser() {
        UserToCreate userToCreate = new UserToCreate("firstname",
                "lastname",
                "username",
                "password");
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(ConflictException.class,()->authenticationService.register(userToCreate));
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void authenticateValidUser() {
        UserToLogin userToLogin = new UserToLogin("username", "password");
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);
        when(userRepository.findByUsername(userToLogin.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticate(userToLogin);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

    }

    @Test
    void authenticateInvalidUser() {
        UserToLogin userToLogin = new UserToLogin("username", "wrongPassword");
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);
        when(userRepository.findByUsername(userToLogin.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(
                new UnauthorizedException("Bad username or password"));

        assertThrows(UnauthorizedException.class,()->authenticationService.authenticate(userToLogin));

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), userToLogin.getPassword())
        );
        verify(jwtService,times(0)).generateToken(user);

    }
}