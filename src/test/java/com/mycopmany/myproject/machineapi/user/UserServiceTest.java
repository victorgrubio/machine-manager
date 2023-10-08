package com.mycopmany.myproject.machineapi.user;

import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers() {
        User user = new User("firstname",
                "lastname",
                "username",
                "password",
                Role.USER);
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(user);
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserToGet> users = userService.getUsers();

        verify(userRepository).findAll();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("firstname", users.get(0).getFirstName());
        assertEquals("lastname",users.get(0).getLastName());
        assertEquals("username", users.get(0).getUsername());

    }

    @Test
    void deleteUserWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).deleteById(1L);

    }

    @Test
    void deleteUserWhenDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,() ->userService.deleteUser(1L));

        verify(userRepository, times(0)).deleteById(1L);
    }
}