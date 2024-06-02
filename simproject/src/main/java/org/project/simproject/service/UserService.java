package org.project.simproject.service;


import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.AddUserRequest;
import org.project.simproject.dto.request.ModifyRequest;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WatchListService watchListService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public User save(AddUserRequest addUserRequest) {
        User user = userRepository.save(
                        User.builder()
                                .email(addUserRequest.getEmail())
                                .password(bCryptPasswordEncoder.encode(addUserRequest.getPassword()))
                                .nickname(addUserRequest.getNickname())
                                .build()
        );
        watchListService.save(addUserRequest.getEmail());

        return user;
    }

    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + email));
    }

    public User findByNickname(String nickname){
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    @Transactional
    public User modify(String email, ModifyRequest modifyRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 : " + email));
        user.modify(modifyRequest, bCryptPasswordEncoder);
        return user;
    }

    @Transactional
    public void delete(String email) {
        watchListService.delete(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        userRepository.delete(user);
    }
}
