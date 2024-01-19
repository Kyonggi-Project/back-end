package org.project.simproject.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.project.simproject.repository.*;
import org.project.simproject.domain.User;
import org.project.simproject.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    public User createUser(String email, String password, AddUserRequest addUserRequest) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 사용 중인 사용자입니다.");
        }

        // 사용자 생성
        User user = new User(email, password, addUserRequest.getNickname());

        // 사용자 저장
        userRepository.save(user);

        return user;
    }
    @Transactional
    public void updateUser(User user, String newEmail, String newPassword, String newNickname) {
        User persistance = userRepository.findById(user.getId()).orElseThrow(()->{
            return new IllegalArgumentException("회원 찾기 실패");
        });
        String rawPassword = user.getPassword();
        String rawEmail = user.getEmail();
        String rawNickname = user.getNickname();
        if(!rawPassword.equals(newPassword) && !rawPassword.isEmpty()) {
            persistance.setPassword(newPassword);
        } else {
            persistance.setPassword(rawPassword);
        }
        if(!rawEmail.equals(newEmail) && !rawEmail.isEmpty()) {
            persistance.setEmail(newEmail);
        } else {
            persistance.setEmail(rawEmail);
        }
        if(!rawNickname.equals(newNickname) && !rawNickname.isEmpty()) {
            persistance.setNickname(newNickname);
        } else {
            persistance.setNickname(rawNickname);
        }
    }

    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + id));
        userRepository.delete(user);
    }

}
