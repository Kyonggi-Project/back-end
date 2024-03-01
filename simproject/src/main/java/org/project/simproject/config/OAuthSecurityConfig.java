package org.project.simproject.config;

import lombok.RequiredArgsConstructor;
import org.project.simproject.config.jwt.JwtTokenProvider;
import org.project.simproject.config.oauth.AuthSuccessHandler;
import org.project.simproject.config.oauth.OAuth2CookieRepository;
import org.project.simproject.config.oauth.OAuth2SuccessHandler;
import org.project.simproject.config.oauth.OAuth2UserCustomService;
import org.project.simproject.repository.RefreshTokenRepository;
import org.project.simproject.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class OAuthSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserService userService;
    private final OAuth2UserCustomService oAuth2UserCustomService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
                .requestMatchers("/v2/api-docs", "/swagger-resources/**",
                        "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception",
                        "/static/**", "/img/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .requestMatchers("/api/token/createToken").permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll();

        http.oauth2Login()
                .loginPage("/oauthLogin")
                .authorizationEndpoint()
                .authorizationRequestRepository(oAuth2CookieRepository())
                .and()
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint()
                .userService(oAuth2UserCustomService);

        http.logout()
                .logoutSuccessUrl("/login");

        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**"));

        http.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .successHandler(authSuccessHandler())
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true);

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                jwtTokenProvider,
                refreshTokenRepository,
                oAuth2CookieRepository(),
                userService
        );
    }

    public AuthSuccessHandler authSuccessHandler(){
        return new AuthSuccessHandler(
                jwtTokenProvider,
                refreshTokenRepository,
                userService
        );
    }

    @Bean
    public TokenFilter tokenFilter() {
        return new TokenFilter(jwtTokenProvider);
    }

    @Bean
    public OAuth2CookieRepository oAuth2CookieRepository() {
        return new OAuth2CookieRepository();
    }
}
