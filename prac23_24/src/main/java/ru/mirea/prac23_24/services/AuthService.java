package ru.mirea.prac23_24.services;

import ru.mirea.prac23_24.dtos.CredentialsDto;
import ru.mirea.prac23_24.dtos.SignUpDto;
import ru.mirea.prac23_24.dtos.TokenDto;
import java.util.UUID;
import org.springframework.data.util.Pair;

public interface AuthService {

  Pair<TokenDto, TokenDto> signUp(SignUpDto signUpDto, UUID idempotencyKey)
      throws IllegalAccessException;

  Pair<TokenDto, TokenDto> signIn(CredentialsDto credentialsDto) throws IllegalAccessException;

  TokenDto reissueAccessToken(String refreshToken) throws IllegalAccessException;

}
