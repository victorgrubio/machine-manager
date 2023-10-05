package com.mycopmany.myproject.machineapi.config;

import com.mycopmany.myproject.machineapi.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    private KeyPair keyPair;
    public JwtService() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(256);
            this.keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String extractUsername(String jwToken){
        return extractClaim(jwToken,Claims::getSubject);
    }

    public String generateToken(User user){
        return generateToken(new HashMap<>(), user);
    }

    public boolean isTokenValid(String jwToken, UserDetails userDetails){
        final String username = extractUsername(jwToken);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwToken);
    }

    private boolean isTokenExpired(String jwToken) {
        return extractExpiration(jwToken).before(new Date());
    }

    private Date extractExpiration(String jwToken) {
        return extractClaim(jwToken,Claims::getExpiration);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            User user
    ){
        return  Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.ES256)
                .compact();
    }

    public String generateTokenWithCustomExpiration(
            Map<String, Object> extraClaims,
            User user,
            Date expirationDate

    ){
        return  Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.ES256)
                .compact();
    }
    public <T> T extractClaim(String jwToken, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jwToken);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String jwToken){
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jwToken)
                .getBody();
    }

}
