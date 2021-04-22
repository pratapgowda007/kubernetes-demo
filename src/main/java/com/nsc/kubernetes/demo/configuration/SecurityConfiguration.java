package com.nsc.kubernetes.demo.configuration;

import com.nsc.kubernetes.demo.security.JWTAuthenticationFilter;
import com.nsc.kubernetes.demo.security.JWTAuthorizationFilter;
import com.nsc.kubernetes.demo.service.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        /*httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                //.antMatchers("/**").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/login**").permitAll()
                .antMatchers("/process_login").permitAll()
                .antMatchers("/user/register").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/api/**").fullyAuthenticated()
                .anyRequest().authenticated()
                .and().httpBasic()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("userId")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .and()
                .logout().permitAll()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.headers().frameOptions().disable();*/
        httpSecurity.authorizeRequests().antMatchers("/**").permitAll().and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(appUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    /* To allow Pre-flight [OPTIONS] request from browser */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
