package ru.mirea.prac23_24.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mirea.prac23_24.configs.UserAuthenticationProvider;
import ru.mirea.prac23_24.configs.UserAuthenticationProvider.TokenType;
import ru.mirea.prac23_24.dtos.CredentialsDto;
import ru.mirea.prac23_24.dtos.SignUpDto;
import ru.mirea.prac23_24.repositories.AuthSessionRepo;
import ru.mirea.prac23_24.repositories.UserRepo;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTest {

  private static final String X_IDEMPOTENCY_KEY = "X-Idempotency-Key";
  private static final String TOKEN_KEY = "token";
  private static final String ERROR_MESSAGE_KEY = "message";
  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
  private static final String SIGNIN_ENDPOINT = "/v1/auth/signin";
  private static final String SIGNUP_ENDPOINT = "/v1/auth/signup";
  private static final String REISSUE_ENDPOINT = "/v1/auth/reissue_access_token";

  private final ObjectMapper objectMapper = new ObjectMapper();
  @LocalServerPort
  private Integer port;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserAuthenticationProvider provider;
  @Autowired
  private UserRepo userRepo;
  @Autowired
  private AuthSessionRepo authSessionRepo;

  @AfterEach
  void eraseDB() {
    userRepo.deleteAll();
    authSessionRepo.deleteAll();
  }

  private static final char[] VALID_PASSWORD = "123".toCharArray();
  private static final char[] INVALID_PASSWORD = "456".toCharArray();
  private static final String VALID_PHONE = "+79999999999";

  private static final SignUpDto VALID_SIGNUP = new SignUpDto("kostuwan", VALID_PHONE,
      VALID_PASSWORD);
  private static final CredentialsDto VALID_CREDENTIALS = new CredentialsDto(VALID_PHONE,
      VALID_PASSWORD);
  private static final CredentialsDto INVALID_CREDENTIALS = new CredentialsDto(VALID_PHONE,
      INVALID_PASSWORD);

  @Test
  void signUp() throws Exception {

    var response = mockMvc.perform(
            post(SIGNUP_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(VALID_SIGNUP))
                .header(X_IDEMPOTENCY_KEY, UUID.randomUUID().toString()))
        .andExpectAll(status().isOk(), cookie().exists(REFRESH_TOKEN_COOKIE),
            content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    var responseBody = objectMapper.readValue(response.getContentAsString(), HashMap.class);
    Assertions.assertTrue(responseBody.containsKey(TOKEN_KEY));
    var refreshToken = response.getCookie(REFRESH_TOKEN_COOKIE).getValue();
    Assertions.assertDoesNotThrow(() -> {
      provider.validateToken(refreshToken, TokenType.REFRESH_TOKEN);
      provider.validateToken(responseBody.get(TOKEN_KEY).toString(), TokenType.ACCESS_TOKEN);
    });
  }

  @Test
  void invalidSignUp() throws Exception {
    mockMvc.perform(
            post(SIGNUP_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(VALID_SIGNUP))
                .header(X_IDEMPOTENCY_KEY, UUID.randomUUID().toString()))
        .andExpectAll(status().isOk(), cookie().exists(REFRESH_TOKEN_COOKIE),
            content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    var response = mockMvc.perform(
            post(SIGNUP_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(VALID_SIGNUP))
                .header(X_IDEMPOTENCY_KEY, UUID.randomUUID().toString()))
        .andExpectAll(status().isUnauthorized(), content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    var responseBody = objectMapper.readValue(response.getContentAsString(), HashMap.class);
    Assertions.assertTrue(responseBody.containsKey(ERROR_MESSAGE_KEY));
    Assertions.assertEquals("User already signUp", responseBody.get(ERROR_MESSAGE_KEY));
  }


  @Test
  void signIn() throws Exception {
    mockMvc.perform(
            post(SIGNUP_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(VALID_SIGNUP))
                .header(X_IDEMPOTENCY_KEY, UUID.randomUUID().toString()))
        .andExpectAll(status().isOk(), cookie().exists(REFRESH_TOKEN_COOKIE),
            content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    var response = mockMvc.perform(
            post(SIGNIN_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(VALID_CREDENTIALS)))
        .andExpectAll(status().isOk(), cookie().exists(REFRESH_TOKEN_COOKIE),
            content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    var responseBody = objectMapper.readValue(response.getContentAsString(), HashMap.class);
    Assertions.assertTrue(responseBody.containsKey(TOKEN_KEY));
    var refreshToken = response.getCookie(REFRESH_TOKEN_COOKIE).getValue();
    Assertions.assertDoesNotThrow(() -> {
      provider.validateToken(refreshToken, TokenType.REFRESH_TOKEN);
      provider.validateToken(responseBody.get(TOKEN_KEY).toString(), TokenType.ACCESS_TOKEN);
    });
  }


  @Test
  void invalidPasswordSignIn() throws Exception {
    mockMvc.perform(
            post(SIGNUP_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(VALID_SIGNUP))
                .header(X_IDEMPOTENCY_KEY, UUID.randomUUID().toString()))
        .andExpectAll(status().isOk(), cookie().exists(REFRESH_TOKEN_COOKIE),
            content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    var response = mockMvc.perform(
            post(SIGNIN_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(INVALID_CREDENTIALS)))
        .andExpectAll(status().isUnauthorized(), content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    var responseBody = objectMapper.readValue(response.getContentAsString(), HashMap.class);
    Assertions.assertTrue(responseBody.containsKey(ERROR_MESSAGE_KEY));
    Assertions.assertEquals("Invalid password", responseBody.get(ERROR_MESSAGE_KEY));
  }


  @Test
  void invalidUserSignIn() throws Exception {
    var response = mockMvc.perform(
            post(SIGNIN_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(VALID_CREDENTIALS)))
        .andExpectAll(status().isUnauthorized(), content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    var responseBody = objectMapper.readValue(response.getContentAsString(), HashMap.class);
    Assertions.assertTrue(responseBody.containsKey(ERROR_MESSAGE_KEY));
    Assertions.assertEquals("User not signup yet", responseBody.get(ERROR_MESSAGE_KEY));
  }


  @Test
  void reissueToken() throws Exception {
    var response = mockMvc.perform(
            post(SIGNUP_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(VALID_SIGNUP))
                .header(X_IDEMPOTENCY_KEY, UUID.randomUUID().toString()))
        .andExpectAll(status().isOk(), cookie().exists(REFRESH_TOKEN_COOKIE),
            content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    var refreshToken = response.getCookie(REFRESH_TOKEN_COOKIE);

    response = mockMvc.perform(post(REISSUE_ENDPOINT).cookie(refreshToken))
        .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    var responseBody = objectMapper.readValue(response.getContentAsString(), HashMap.class);
    Assertions.assertTrue(responseBody.containsKey(TOKEN_KEY));
    var accessToken = responseBody.get(TOKEN_KEY).toString();

    Assertions.assertDoesNotThrow(() -> {
      provider.validateToken(accessToken, TokenType.ACCESS_TOKEN);
    });
  }

  @Test
  void invalidReissueTokenWithOldToken() throws Exception {
    var response = mockMvc.perform(
            post(REISSUE_ENDPOINT).cookie(new Cookie(REFRESH_TOKEN_COOKIE, "aboba")))
        .andExpectAll(status().isUnauthorized(), content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    var responseBody = objectMapper.readValue(response.getContentAsString(), HashMap.class);
    Assertions.assertTrue(responseBody.containsKey(ERROR_MESSAGE_KEY));
    var message = responseBody.get(ERROR_MESSAGE_KEY).toString();
    Assertions.assertEquals("Invalid refresh token", message);
  }
}
