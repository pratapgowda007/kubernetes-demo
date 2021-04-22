package com.nsc.kubernetes.demo.controller;

import com.auth0.jwt.JWT;
import com.nsc.kubernetes.demo.model.AppUser;
import com.nsc.kubernetes.demo.repository.UserRepository;
import io.swagger.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(path = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppUser register(@RequestBody AppUser appUser) {

        String encryptedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encryptedPassword);
        return userRepository.save(appUser);
    }

    /**
     * Roles (as they are used in many examples) are just "permissions" with a naming convention that says that
     * a role is a GrantedAuthority that starts with the prefix ROLE_
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(path = "/get/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public AppUser register(@PathVariable String userId) {
        Optional<AppUser> appUserOptional = userRepository.findById(userId);
        return appUserOptional.get();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(path = "/token", method = RequestMethod.GET)
    public ResponseEntity<String> getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            String token = JWT.create()
                    .withClaim("USER_ROLES", roles)
                    .withSubject(authentication.getName())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 2000000))
                    .sign(HMAC512("SECRET".getBytes(StandardCharsets.UTF_8)));
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>("AnonymousAuthenticationToken found", HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/subquery", method = RequestMethod.GET)
    public List<AppUser> test() {
        return userRepository.getUserBySubQueryExample();
    }
}
