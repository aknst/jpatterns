package ru.mirea.prac23_24.controllers;

import ru.mirea.prac23_24.dtos.CredentialsDto;
import ru.mirea.prac23_24.dtos.SignUpDto;
import ru.mirea.prac23_24.exceptions.AppException;
import ru.mirea.prac23_24.exceptions.InvalidClaimsException;
import ru.mirea.prac23_24.exceptions.InvalidPasswordException;
import ru.mirea.prac23_24.exceptions.InvalidTokenException;
import ru.mirea.prac23_24.exceptions.ResourceNotFoundException;
import ru.mirea.prac23_24.exceptions.TokenExpiresException;
import ru.mirea.prac23_24.exceptions.UserAlreadySignUpException;
import ru.mirea.prac23_24.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/v1/auth")
public class AuthController {

  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
  private AuthService authService;

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(HttpServletResponse response,
      @RequestBody CredentialsDto credentialsDto) throws IllegalAccessException {
    try {
      var tokens = authService.signIn(credentialsDto);
      var refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, tokens.getFirst().getToken());
      response.addCookie(refreshTokenCookie);
      return ResponseEntity.ok(tokens.getSecond());
    } catch (ResourceNotFoundException | InvalidPasswordException e) {
      log.warn(e.getMessage());
      throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/reissue_access_token")
  public ResponseEntity<?> reissueAccessToken(@CookieValue String refreshToken)
      throws IllegalAccessException {
    try {
      return ResponseEntity.ok(authService.reissueAccessToken(refreshToken));
    } catch (InvalidClaimsException | TokenExpiresException | InvalidTokenException e) {
      log.warn(e.getMessage());
      throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signUp(HttpServletResponse response,
      @RequestBody SignUpDto signUpDto,
      @RequestHeader(name = "X-Idempotency-Key") String idempotencyKey)
      throws IllegalAccessException {
    try {
      var tokens = authService.signUp(signUpDto, UUID.fromString(idempotencyKey));
      var refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, tokens.getFirst().getToken());
      response.addCookie(refreshTokenCookie);
      return ResponseEntity.ok(tokens.getSecond());
    } catch (UserAlreadySignUpException e) {
      log.warn(e.getMessage());
      throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }
}
