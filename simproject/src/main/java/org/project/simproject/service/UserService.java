package org.project.simproject.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.project.simproject.repository.*;
import org.project.simproject.domain.User;
import org.project.simproject.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User createUser(AddUserRequest addUserRequest) {
        return userRepository.save(addUserRequest.toEntity());
    }
    @Transactional
    public User updateUser(String email, ModifyRequest modifyRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + email));
        user.modify(modifyRequest);
        return user;
    }

    public User showUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + email));
    }

    public void delete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + email));
        userRepository.delete(user);
    }
}
