package com.example.https.transform.mapper;

import com.example.https.dto.UserDTO;
import com.example.https.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getAge(), user.getEmail(), user.getUuid());
    }

    public User toModel(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getAge(), userDTO.getEmail(), userDTO.getUuid());
    }
}
