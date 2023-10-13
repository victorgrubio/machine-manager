package com.mycopmany.myproject.machineapi.user;

import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<UserToGet> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserToGet::fromModel)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id){
        boolean exists = userRepository.existsById(id);
        if (!exists){
            throw new ResourceNotFoundException("User with id: " + id + "does not exist");
        }
        userRepository.deleteById(id);
    }



}
