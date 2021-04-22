package com.nsc.kubernetes.demo.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsc.kubernetes.demo.model.AppUser;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            AppUser appUser = new AppUser();
            if ("application/x-www-form-urlencoded".equals(req.getHeader(HttpHeaders.CONTENT_TYPE))) {
                appUser.setUserId(req.getParameter("userId"));
                appUser.setPassword(req.getParameter("password"));
            } else {
                String data = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                appUser = new ObjectMapper().readValue(data, AppUser.class);
            }
            List<GrantedAuthority> grantedAuthorityList = appUser.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .distinct().collect(Collectors.toList());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(appUser.getUserId(), appUser.getPassword(), grantedAuthorityList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        User user = ((User) auth.getPrincipal());
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String token = JWT.create()
                .withClaim("USER_ROLES", roles)
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 2000000))
                .sign(HMAC512("SECRET".getBytes(StandardCharsets.UTF_8)));
        res.addHeader("Authorization", "Bearer " + token);
        res.setContentType("application/json");
        res.getWriter().write("{\"token\": \""+token+"\"}");
        res.getWriter().flush();
        res.getWriter().close();
    }
}
