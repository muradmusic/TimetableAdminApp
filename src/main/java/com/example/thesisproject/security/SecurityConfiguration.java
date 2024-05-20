package com.example.thesisproject.security;

import com.example.thesisproject.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfiguration {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
    
        http.formLogin(formLogin -> formLogin
                                .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                          .requestMatchers("/").permitAll()  
                                .requestMatchers("/users/**").hasRole("ADMIN")
                                .requestMatchers("/courses/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .successHandler(customLoginSuccessHandler(userRepository)))

            .exceptionHandling((exceptionHandling) ->
                    exceptionHandling
                            .accessDeniedPage("/403"))

            .logout(logout -> logout.permitAll()
                    .invalidateHttpSession(true) 
                    .deleteCookies("JSESSIONID")); 

    return http.build();
}

    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler(UserRepository userRepository) {
        return new CustomLoginSuccessHandler(userRepository);
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }


}
