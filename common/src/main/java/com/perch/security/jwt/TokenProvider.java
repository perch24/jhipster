package com.perch.security.jwt;

import com.perch.config.CommonProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

  private static final String AUTHORITIES_KEY = "auth";
  private final Logger log = LoggerFactory.getLogger(TokenProvider.class);
  private String secretKey;

  private long tokenValidityInMilliseconds;

  private long tokenValidityInMillisecondsForRememberMe;

  @Inject
  private CommonProperties jHipsterProperties;

  @PostConstruct
  public void init() {
    this.secretKey =
      jHipsterProperties.getSecurity().getAuthentication().getJwt().getSecret();

    this.tokenValidityInMilliseconds =
      1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
    this.tokenValidityInMillisecondsForRememberMe =
      1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
  }

  public String createToken(Authentication authentication, Boolean rememberMe) {
    String authorities = authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity;
    if (rememberMe) {
      validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
    } else {
      validity = new Date(now + this.tokenValidityInMilliseconds);
    }

    return Jwts.builder()
      .setSubject(authentication.getName())
      .claim(AUTHORITIES_KEY, authorities)
      .signWith(SignatureAlgorithm.HS512, secretKey)
      .setExpiration(validity)
      .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parser()
      .setSigningKey(secretKey)
      .parseClaimsJws(token)
      .getBody();

    Collection<? extends GrantedAuthority> authorities =
      Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    User principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      log.info("Invalid JWT signature: " + e.getMessage());
      return false;
    }
  }
}
