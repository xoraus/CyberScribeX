package com.xoraus.cyberscribex.security;

import com.xoraus.cyberscribex.exception.CyberScribeXAPIException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-milliseconds}")
    private long jwtExpirationDate;

    // generate JWT Token
    public String generateToken(Authentication auth) {
        String username = auth.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);

            return true;
        } catch(MalformedJwtException malformedJwtException){
            throw new CyberScribeXAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
        } catch(ExpiredJwtException expiredJwtException) {
            throw  new CyberScribeXAPIException(HttpStatus.BAD_REQUEST, "Expired JWT Token");
        } catch(UnsupportedJwtException unsupportedJwtException){
            throw  new CyberScribeXAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT Token");
        } catch(IllegalArgumentException illegalArgumentException){
            throw new CyberScribeXAPIException(HttpStatus.BAD_REQUEST, "Illegal JWT Token");
        }
    }
}
