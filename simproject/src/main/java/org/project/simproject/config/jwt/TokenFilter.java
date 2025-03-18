package org.project.simproject.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.simproject.config.auth.PrincipalDetailsService;
import org.project.simproject.domain.User;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class TokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenService;

    private final UserRepository userRepository;

    private final PrincipalDetailsService detailsService;

    private static final String NOT_CHECK_URL = "/login"; // /login으로 들어오는 요청 필터링 제외

    //필터 적용 시작
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NOT_CHECK_URL)){ // 로그인 요청 시, 해당 필터를 무시하고 다음 필터로 진행
            log.info("일반(자체) 로그인 요청");
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtTokenService.resolveRefreshToken(request)
                .filter(jwtTokenService::validateToken)
                .orElse(null);

        if(refreshToken != null){
            log.info("Access 토큰 재발급 진행");
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        if(refreshToken == null){
            log.info("유저 인증 절차 진행");
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    // 액세스 토큰 추출 및 유효성 검사
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException{
        jwtTokenService.resolveAccessToken(request)
                .filter(jwtTokenService::validateToken)
                .ifPresent(accessToken -> jwtTokenService.getUsername(accessToken)
                        .ifPresent(username -> userRepository.findByEmail(username)
                                .ifPresent(this::saveAuthentication)));

        filterChain.doFilter(request, response);
    }

    // 인증 객체 생성 및 저장
    public void saveAuthentication(User user){
        UserDetails userDetails = detailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 리프레시 토큰을 이용해 유저 검색 및 액세스 토큰 재발급
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken){
        userRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String reIssueRefreshToken = reIssueRefreshToken(user);
                    jwtTokenService.sendAccessAndRefreshToken(response,
                            jwtTokenService.createToken(user, Duration.ofMinutes(30)), reIssueRefreshToken);
                });
    }

    // 리프레시 토큰도 같이 재발급
    public String reIssueRefreshToken(User user){
        String reIssueRefreshToken = jwtTokenService.createToken(user, Duration.ofDays(7));
        user.updateRefreshToken(reIssueRefreshToken);
        userRepository.saveAndFlush(user);

        return reIssueRefreshToken;
    }
}
