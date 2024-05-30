package ru.mirea.prac23_24.services.impl;

import ru.mirea.prac23_24.configs.UserAuthenticationProvider;
import ru.mirea.prac23_24.dtos.CredentialsDto;
import ru.mirea.prac23_24.dtos.SignUpDto;
import ru.mirea.prac23_24.dtos.TokenDto;
import ru.mirea.prac23_24.entities.AuthSession;
import ru.mirea.prac23_24.exceptions.InvalidPasswordException;
import ru.mirea.prac23_24.exceptions.InvalidTokenException;
import ru.mirea.prac23_24.exceptions.ResourceNotFoundException;
import ru.mirea.prac23_24.exceptions.UserAlreadySignUpException;
import ru.mirea.prac23_24.mappers.UserMapper;
import ru.mirea.prac23_24.repositories.AuthSessionRepo;
import ru.mirea.prac23_24.repositories.UserRepo;
import ru.mirea.prac23_24.services.AuthService;
import java.nio.CharBuffer;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserAuthenticationProvider provider;

  private final UserRepo userRepo;
  private final AuthSessionRepo authSessionRepo;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Pair<TokenDto, TokenDto> signUp(SignUpDto signUpDto, UUID idempotencyKey) {
    var optionalUser = userRepo.findByPhone(signUpDto.getPhone());

    if (optionalUser.isPresent()) {
      throw new UserAlreadySignUpException("User already signUp");
    }

    var user = userMapper.toUser(signUpDto);
    var userId = UUID.randomUUID();
    var sessionId = UUID.randomUUID();

    user.setId(userId);
    user.setIdempotencyKey(idempotencyKey);
    user.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.getPassword())));
    log.debug(
      "SOME MESSAGE"
    );
    var saved = userRepo.save(user);

    var tokens = provider.createTokens(userId, sessionId);
    var authSession = new AuthSession();
    authSession.setId(sessionId);
    authSession.setRefreshToken(tokens.getFirst());
    authSession.setIdempotencyKey(idempotencyKey);
    authSession.setUser(saved);

    var session = authSessionRepo.save(authSession);

    saved.setAuthSession(session);

    userRepo.save(saved);

    return Pair.of(TokenDto.builder().token(tokens.getFirst()).build(),
        TokenDto.builder().token(tokens.getSecond()).build());
  }

  @Override
  public Pair<TokenDto, TokenDto> signIn(CredentialsDto credentialsDto) {
    var optionalUser = userRepo.findByPhone(credentialsDto.getPhone());

    if (optionalUser.isEmpty()) {
      throw new ResourceNotFoundException("User not signup yet");
    }

    var user = optionalUser.get();

    if (!passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()),
        user.getPassword())) {
      throw new InvalidPasswordException("Invalid password");
    }

    var session = user.getAuthSession();

    var tokens = provider.createTokens(user.getId(), session.getId());

    session.setRefreshToken(tokens.getFirst());
    authSessionRepo.save(session);

    return Pair.of(TokenDto.builder().token(tokens.getFirst()).build(),
        TokenDto.builder().token(tokens.getSecond()).build());
  }

  @Override
  public TokenDto reissueAccessToken(String refreshToken) throws IllegalAccessException {
    var optAuthSession = authSessionRepo.findAuthSessionByRefreshToken(refreshToken);
    if (optAuthSession.isEmpty()) {
      throw new InvalidTokenException("Invalid refresh token");
    }
    return provider.reissueAccessToken(refreshToken);
  }
}
