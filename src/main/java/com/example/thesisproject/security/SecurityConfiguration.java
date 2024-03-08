package com.example.thesisproject.security;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfiguration {


    @Autowired
    private UserDetailsService userDetailsService;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.formLogin(formLogin -> formLogin
////                        .loginPage("/login")  // Path to the custom login page
////                        .loginProcessingUrl("/login")  // URL to submit the login form
//                                .permitAll()  // Allow all users to access the login page
//                )
//                // Disable CSRF for simplicity, consider enabling and configuring for production
//                .csrf(AbstractHttpConfigurer::disable)
//                // Configure authorization rules
//
//                .authorizeHttpRequests(auth -> auth
////                      .requestMatchers("/").permitAll()  // Home page accessible by anyone
////                        .anyRequest().authenticated()
//                                .requestMatchers("/subjects/**").hasRole("Admin")  // Admin section requires ADMIN role
//                                .requestMatchers("/users/**").hasRole("Teacher")  // User section requires USER role
//                                .anyRequest().authenticated()  // All other requests need to be authenticated
//                )
//                // Configure form login
//                .formLogin(form -> form
////                        .loginPage("/login")  // Custom login page
//                                .defaultSuccessUrl("/")  // Redirect after successful login
//                                .permitAll()  // Allow everyone to see login page
//                )
//                .exceptionHandling((exceptionHandling) ->
//                        exceptionHandling
//                                .accessDeniedPage("/403"))
//                // Configure logout
//                .logout(logout -> logout.permitAll()
//                        .invalidateHttpSession(true) // Invalidate session
//                        .deleteCookies("JSESSIONID")); // Delete session cookie);  // Allow everyone to logout
//
//        return http.build();
//    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .authorizeHttpRequests((authorize) ->
                    authorize.requestMatchers("/").permitAll().anyRequest().authenticated()
//                            .requestMatchers("/users/**").hasRole("ROLE_ADMIN").anyRequest()
//                            .requestMatchers("/subjects/**").authenticated()
//                            .requestMatchers("/users/**").hasRole("Admin")
//                            .anyRequest()
            ).formLogin(
                    form -> form
                            .defaultSuccessUrl("/subjects/all")
                            .permitAll()
            ).logout(
                    logout -> logout
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .permitAll()
            );
    return http.build();
}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
