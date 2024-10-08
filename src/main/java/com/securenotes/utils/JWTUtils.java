package com.securenotes.utils;

import com.securenotes.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JWTUtils {
    //step1: define your secret key

    private final SecretKey key;

    @Autowired
    TokenRepository tokenRepository;

    private static final long EXPIRATION_TIME = 86400000;//24 HOURSS

    public JWTUtils(){
        String secretString = "2342349324732874932749dfg34343343274832942384324";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyBytes,"HmacSHA256");
    }

    //step 2: make generate token and refresh token function
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }


    public String generateRefreshToken(HashMap<String, Object> claims,UserDetails userDetails){

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(key)
                .compact();

    }

    //step 3: make function to extract usernames and claims from token

    private<T> T extractClaims(String token, Function<Claims, T> claimsTFunction){

        return claimsTFunction.apply(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody());
    }

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

//step 4: check token validity and expiration

    public boolean isTokenExpired(String token){
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        //means token should not be logged out from token class
        boolean validToken = tokenRepository.findByToken(token)
                .map(t->!t.isLoggedOut()).orElse(false);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && validToken;
    }

}
