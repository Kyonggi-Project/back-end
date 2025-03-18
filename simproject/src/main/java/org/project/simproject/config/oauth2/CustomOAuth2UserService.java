package org.project.simproject.config.oauth2;

import lombok.RequiredArgsConstructor;
import org.project.simproject.config.auth.PrincipalDetails;
import org.project.simproject.domain.User;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuthUser(userRequest, oAuth2User);
    }

    public OAuth2User processOAuthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
        OAuth2UserInfo userInfo = null;

        // provider의 종류에 따라 속성 추출 방식 구분
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            userInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }

        User user = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .orElse(null);

        if(user == null){
            user = User.builder()
                    .nickname(userInfo.getProvider()+userInfo.getProviderId())
                    .email(userInfo.getEmail())
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .build();
            userRepository.saveAndFlush(user);
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
