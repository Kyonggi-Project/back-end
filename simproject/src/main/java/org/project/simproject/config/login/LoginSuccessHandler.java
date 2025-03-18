package org.project.simproject.config.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.simproject.config.auth.PrincipalDetails;
import org.project.simproject.config.jwt.JwtTokenProvider;
import org.project.simproject.domain.User;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.project.simproject.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final Duration ACCESS_DURATION = Duration.ofMinutes(30);
    private static final Duration REFRESH_DURATION = Duration.ofDays(7);
    public static final String REDIRECT_PATH = "http://localhost:3000/userprofile";         // TargetUrl 추후 설정

    private final JwtTokenProvider jwtTokenService;

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = userRepository.findByEmail(getUsername(authentication))
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        String accessToken = jwtTokenService.createToken(user, ACCESS_DURATION);
        String refreshToken = jwtTokenService.createToken(user, REFRESH_DURATION);
        addRefreshTokenToCookie(request, response, refreshToken);

        jwtTokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        user.updateRefreshToken(refreshToken);
        userRepository.saveAndFlush(user);

        log.info("로그인 계정 : "+user.getEmail());
        log.info("Access Token : "+accessToken);
        log.info("Refresh Token : "+refreshToken);

        String targetUrl = getTargetUrl(accessToken);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("redirectUrl", targetUrl);

        String jsonResponse = new ObjectMapper().writeValueAsString(responseData);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }

    public String getUsername(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        return principalDetails.getUsername();
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }


    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toString();
    }
}
