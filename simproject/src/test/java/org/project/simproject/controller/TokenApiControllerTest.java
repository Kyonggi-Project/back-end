package org.project.simproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.simproject.config.jwt.JwtFactory;
import org.project.simproject.config.jwt.Properties;
import org.project.simproject.domain.RefreshToken;
import org.project.simproject.domain.User;
import org.project.simproject.dto.request.RefreshTokenRequest;
import org.project.simproject.repository.RefreshTokenRepository;
import org.project.simproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Properties properties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void beforeEach(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("리프레시 토큰을 이용한 액세스 토큰 재발급")
    @Test
    public void test() throws Exception{
            final String url = "/api/token/createToken";

            User user = User.builder()
                    .email("test@gmail.com")
                    .password("test")
                    .nickname("test")
                    .build();
            userRepository.save(user);

            String refreshToken = JwtFactory.builder()
                    .claims(Map.of("id", user.getId()))
                    .build()
                    .createToken(properties);
            refreshTokenRepository.save(new RefreshToken(user, refreshToken));

            final RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

            final String requestBody = objectMapper.writeValueAsString(request);

            final ResultActions actions = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            actions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty());

    }


}