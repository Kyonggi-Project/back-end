/*package org.project.simproject.config.oauth;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.domain.WatchList;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.project.simproject.repository.mongoRepo.WatchListRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final WatchListRepository watchListRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);

        return user;
    }

    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(nickname))
                .orElse(User.builder()
                        .email(email)
                        .nickname(nickname)
                        .build());

        if(!watchListRepository.existsWatchListByEmail(email)){
            watchListRepository.save(
                    WatchList.builder()
                            .email(email)
                            .build()
            );
        }

        return userRepository.save(user);
    }
}*/
