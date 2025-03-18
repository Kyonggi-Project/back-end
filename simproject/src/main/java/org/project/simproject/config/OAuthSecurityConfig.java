package org.project.simproject.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.project.simproject.config.auth.PrincipalDetailsService;
import org.project.simproject.config.jwt.JwtTokenProvider;
import org.project.simproject.config.jwt.TokenFilter;
import org.project.simproject.config.login.CustomJsonUsernamePasswordAuthenticationFilter;
import org.project.simproject.config.login.LoginFailureHandler;
import org.project.simproject.config.login.LoginSuccessHandler;
import org.project.simproject.config.oauth.*;
import org.project.simproject.config.oauth2.CustomOAuth2UserService;
import org.project.simproject.config.oauth2.OAuth2FailureHandler;
import org.project.simproject.config.oauth2.OAuth2SuccessHandler;
import org.project.simproject.repository.entityRepo.RefreshTokenRepository;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.project.simproject.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class OAuthSecurityConfig {

    /*private final JwtTokenProvider jwtTokenProvider;

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
        http.cors().configurationSource(corsConfigurationSource());
        http.csrf().disable();
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .requestMatchers("/api/token/createToken").permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll();

        // localhost:3000에서 오는 사용자 인증 처리 요청 처리 부분
        http.oauth2Login()
                .loginPage("/oauthLogin")
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")     // 사용자 인증 엔드 포인트 설정
                .authorizationRequestRepository(oAuth2CookieRepository())
                .and()
                .redirectionEndpoint()            // 리다이렉트 엔드 포인트 설정
                .baseUri("/oauth2/callback/*")    // 리다이렉트 URI
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
                .failureHandler(new CustomAuthenticationFailureHandler())
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PrincipalDetailsService principalDetailsService;
    private final JwtTokenProvider jwtTokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
                .requestMatchers("/v2/api-docs", "/swagger-resources/**",
                        "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception",
                        "/static/**", "/img/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.cors().configurationSource(corsConfigurationSource());
        http.formLogin((formLogin) -> formLogin.disable());
        http.httpBasic((httpBasic) -> httpBasic.disable());
        http.csrf((csrf) -> csrf.disable()); // dev 시, 비활성화
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()));

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll());

        http.oauth2Login((oauth2Login) ->
                oauth2Login
                        .loginPage("/oauthLogin")
                        .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")     // 사용자 인증 엔드 포인트 설정
                        .and()
                        .redirectionEndpoint()            // 리다이렉트 엔드 포인트 설정
                        .baseUri("/oauth2/callback/*")    // 리다이렉트 URI
                        .and()
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                        .userInfoEndpoint((endpoint) -> endpoint.userService(customOAuth2UserService)));

        http.logout()
                .logoutSuccessUrl("/login");

        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**"));

        // 자체 필터 시큐리티 필터에 추가
        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(tokenFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(principalDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler(){
        return new LoginSuccessHandler(jwtTokenService, userRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter(){
        CustomJsonUsernamePasswordAuthenticationFilter filter = new CustomJsonUsernamePasswordAuthenticationFilter(
                objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        filter.setAuthenticationFailureHandler(loginFailureHandler());
        return filter;
    }

    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter(jwtTokenService, userRepository, principalDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
