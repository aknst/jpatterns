package ru.mirea.prac23_24.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import ru.mirea.prac23_24.dtos.TokenDto;
import ru.mirea.prac23_24.exceptions.InvalidClaimsException;
import ru.mirea.prac23_24.exceptions.InvalidTokenException;
import ru.mirea.prac23_24.exceptions.TokenExpiresException;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserAuthenticationProvider {

  private static final List<String> EXPECTED_CLAIMS = List.of("exp", "userId", "sessionId");
  @Value("${service-secrets.refresh-token.secret-key}")
  private String refreshTokenSecretKey;
  @Value("${service-secrets.refresh-token.ttl.amount}")
  private Long refreshTokenTtlAmount;
  @Value("${service-secrets.refresh-token.ttl.chrono-unit}")
  private String refreshTokenTtlChronoUnit;
  @Value("${service-secrets.access-token.secret-key}")
  private String accessTokenSecretKey;
  @Value("${service-secrets.access-token.ttl.amount}")
  private Long accessTokenTtlAmount;
  @Value("${service-secrets.access-token.ttl.chrono-unit}")
  private String accessTokenTtlChronoUnit;
  private Algorithm refreshTokenAlg;
  private Algorithm accessTokenAlg;

  @PostConstruct
  protected void init() {
    refreshTokenSecretKey = Base64.getEncoder().encodeToString(refreshTokenSecretKey.getBytes());
    accessTokenSecretKey = Base64.getEncoder().encodeToString(accessTokenSecretKey.getBytes());
    refreshTokenAlg = Algorithm.HMAC512(refreshTokenSecretKey);
    accessTokenAlg = Algorithm.HMAC512(accessTokenSecretKey);
  }

  private String signToken(UUID userId, UUID sessionId, TokenType tokenType) {
    Algorithm current = switch (tokenType) {
      case ACCESS_TOKEN -> accessTokenAlg;
      case REFRESH_TOKEN -> refreshTokenAlg;
    };
    Instant expiresAt = switch (tokenType) {
      case ACCESS_TOKEN -> Instant.now()
          .plus(accessTokenTtlAmount, ChronoUnit.valueOf(accessTokenTtlChronoUnit.toUpperCase()));
      case REFRESH_TOKEN -> Instant.now()
          .plus(refreshTokenTtlAmount, ChronoUnit.valueOf(refreshTokenTtlChronoUnit.toUpperCase()));
    };
    return JWT.create()
        .withExpiresAt(expiresAt)
        .withClaim(EXPECTED_CLAIMS.get(1), userId.toString())
        .withClaim(EXPECTED_CLAIMS.get(2), sessionId.toString())
        .sign(current);
  }

  public Pair<String, String> createTokens(UUID userId, UUID sessionId) {
    var refreshToken = signToken(userId, sessionId, TokenType.REFRESH_TOKEN);

    var accessToken = signToken(userId, sessionId, TokenType.ACCESS_TOKEN);

    return Pair.of(refreshToken, accessToken);
  }

  public TokenDto reissueAccessToken(String refreshToken) {
    try {
      var refreshTokenClaims = validateToken(refreshToken, TokenType.REFRESH_TOKEN);
      var accessToken = signToken(
          UUID.fromString(refreshTokenClaims.get(EXPECTED_CLAIMS.get(1)).asString()),
          UUID.fromString(refreshTokenClaims.get(EXPECTED_CLAIMS.get(2)).asString()),
          TokenType.ACCESS_TOKEN);
      return TokenDto.builder().token(accessToken).build();
    } catch (JWTVerificationException e) {
      log.warn(e.getMessage());
      throw new InvalidTokenException("Invalid token");
    }
  }

  public Map<String, Claim> validateToken(String token, TokenType tokenType)
      throws IllegalArgumentException {
    var decodedToken = JWT.decode(token);
    switch (tokenType) {
      case REFRESH_TOKEN -> refreshTokenAlg.verify(decodedToken);
      case ACCESS_TOKEN -> accessTokenAlg.verify(decodedToken);
      default -> throw new IllegalArgumentException("Unknown token type");
    }

    var claims = decodedToken.getClaims();
    for (var claim : EXPECTED_CLAIMS) {
      if (!claims.containsKey(claim)) {
        throw new InvalidClaimsException(String.format("Claim %s not found", claim));
      }
    }
    var expAt = Instant.ofEpochSecond(claims.get(EXPECTED_CLAIMS.getFirst()).as(Long.class), 1);
    if (Instant.now().isAfter(expAt)) {
      throw new TokenExpiresException("Token is expires");
    }
    return claims;
  }

  public enum TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN
  }
}
