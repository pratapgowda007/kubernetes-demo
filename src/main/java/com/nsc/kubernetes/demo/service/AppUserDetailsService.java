package com.nsc.kubernetes.demo.service;

import com.nsc.kubernetes.demo.model.AppUser;
import com.nsc.kubernetes.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<AppUser> userOptional = userRepository.findById(s);
        if (userOptional.isPresent()) {
            AppUser appUser = userOptional.get();
            List<SimpleGrantedAuthority> simpleGrantedAuthorityList = simpleGrantedAuthorityList = appUser.getRoles().stream().map(role ->
                    new SimpleGrantedAuthority("ROLE_" + role)).distinct().collect(Collectors.toList());
            Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>(simpleGrantedAuthorityList);

            List<GrantedAuthority> grantedAuthorities = new ArrayList<>(grantedAuthoritySet);
            return new User(appUser.getUserId(), appUser.getPassword(), true, true, true, true, grantedAuthorities);
        }
        return null;
    }
}
