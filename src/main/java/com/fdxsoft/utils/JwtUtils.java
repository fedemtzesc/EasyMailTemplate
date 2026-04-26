package com.fdxsoft.utils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;


@Component
public class JwtUtils {
	@Value("${spring.security.jwt.private.key}")
	private String jwtPrivateKey;
	
	@Value("${spring.security.jwt.user.generator}")
	private String jwtUserGenerator;
	
	
	public String createToken(Authentication authentication) {
		Algorithm algorithm = Algorithm.HMAC256(jwtPrivateKey);
		
		String userName = authentication.getPrincipal().toString();
		
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		
		String jwtToken = JWT.create()
				.withIssuer(this.jwtUserGenerator)
				.withSubject(userName)
				.withClaim("authorities",authorities)
				.withIssuedAt(new Date())
				.withExpiresAt(new Date(System.currentTimeMillis()+1800000))
				.withJWTId(UUID.randomUUID().toString())
				.withNotBefore(new Date(System.currentTimeMillis()))
				.sign(algorithm);
		
		return jwtToken;
	}
	
	public DecodedJWT validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(jwtPrivateKey);
			
			JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer(jwtUserGenerator)
					.build();
			
			DecodedJWT decodedJWT = verifier.verify(token);
			
			return decodedJWT;
		} catch (JWTVerificationException e) {
			throw new JWTVerificationException("Token Invalido! No tiene autorizacion para realzar esta operacion.");
		}
	}
	
	public String extractUsername(DecodedJWT decodedJWT) {
		return decodedJWT.getSubject().toString();
	}
	
	public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
		return decodedJWT.getClaim(claimName);
	}
	
	public Map<String, Claim> extractAllClaims(DecodedJWT decodedJWT){
		return decodedJWT.getClaims();
	}
}	
