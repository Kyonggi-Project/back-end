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
    public User updateUser(Long id,ModifyRequest modifyRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + id));
        user.modify(modifyRequest);
        return user;
    }

    public User showUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + id));
    }

    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + id));
        userRepository.delete(user);
    }
}
