package com.nsc.kubernetes.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsc.kubernetes.demo.model.AppUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws JsonProcessingException {
        String token = request.getHeader("Authorization");
        if (token != null) {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("SECRET".getBytes()))
                    .build()
                    .verify(token.replace("Bearer ", ""));

            String user = decodedJWT.getSubject();
            if (user != null) {
                List<String> roles = decodedJWT.getClaim("USER_ROLES").asList(String.class);
                List<GrantedAuthority> grantedAuthorityList = roles.stream().map(SimpleGrantedAuthority::new)
                        .distinct().collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(user, null, grantedAuthorityList);
            }
            return null;
        }
        return null;
    }
}
