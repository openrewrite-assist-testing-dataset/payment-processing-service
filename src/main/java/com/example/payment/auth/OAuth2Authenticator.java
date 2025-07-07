package com.example.payment.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.security.Principal;
import java.util.Optional;

public class OAuth2Authenticator implements Authenticator<String, Principal> {
    
    private final JWTVerifier jwtVerifier;
    
    public OAuth2Authenticator() {
        // In production, this should come from configuration
        Algorithm algorithm = Algorithm.HMAC256("payment-service-secret-key-change-in-production");
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer("payment-service")
                .build();
    }
    
    @Override
    public Optional<Principal> authenticate(String token) throws AuthenticationException {
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            String subject = jwt.getSubject();
            
            if (subject != null && !subject.trim().isEmpty()) {
                return Optional.of(new OAuth2Principal(subject, jwt));
            }
            
            return Optional.empty();
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
    
    public static class OAuth2Principal implements Principal {
        private final String name;
        private final DecodedJWT jwt;
        
        public OAuth2Principal(String name, DecodedJWT jwt) {
            this.name = name;
            this.jwt = jwt;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        public DecodedJWT getJwt() {
            return jwt;
        }
        
        public String getClaim(String claimName) {
            return jwt.getClaim(claimName).asString();
        }
    }
}