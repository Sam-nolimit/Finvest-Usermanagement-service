package com.prunny.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Define the secret key used for signing and verifying JWT tokens
    private static final String SECRET_KEY = "c0814cdb1342d499b28962c294d205651792fbc9272f35c98dba80c6e413f435";

    // Extract the username from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract a specific claim from the JWT token using a claims resolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate a JWT token without extra claims but only from UserDetails
    public String generateToken( UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generate a JWT token with extra claims and UserDetails
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extraClaims) // Set any additional claims provided
                .setSubject(userDetails.getUsername()) // Set the subject of the token to the username
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the token issue time
                .setExpiration(new Date(System.currentTimeMillis() +1000 * 60 * 24)) // Set token expiration time (24 hours)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign the token with the secret key using HMAC SHA-256
                .compact(); //generate and return the token
    }

    // Validating a token
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract all claims from the JWT token
    private Claims extractAllClaims (String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // Set the key used to verify the signature of the token
                .build()
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Get the body of the token which contains the claims
    }

    // Retrieve the signing key used for JWT token generation and verification
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode the secret key from Base64
        return Keys.hmacShaKeyFor(keyBytes); // Return a Key object for HMAC SHA-256 algorithm
    }
}
