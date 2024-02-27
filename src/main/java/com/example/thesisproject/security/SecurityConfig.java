
package com.example.thesisproject.security;
//import com.example.thesisproject.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.availability.AvailabilityChangeEvent;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//public class SecurityConfig {
//
//    @Autowired
//    UserService userService;
//
//
//
//    com.example.thesisproject.datamodel.entity.User user = new com.example.thesisproject.datamodel.entity.User();
//
//
//@Bean
//public PasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
//}
//@Bean
//public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
//    UserDetails userDetails = User.withUsername("mammamur")
//            .password(passwordEncoder.encode("pass"))
//            .roles("USER")
//            .build();
//
//    return new InMemoryUserDetailsManager(userDetails);
//}
//
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.formLogin(formLogin -> formLogin
//                        .loginPage("/login")  // Path to the custom login page
//                        .loginProcessingUrl("/login")  // URL to submit the login form
//                        .permitAll()  // Allow all users to access the login page
//                )
//                // Disable CSRF for simplicity, consider enabling and configuring for production
//                .csrf(AbstractHttpConfigurer::disable)
//                // Configure authorization rules
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/").permitAll()  // Home page accessible by anyone
//                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Admin section requires ADMIN role
//                        .requestMatchers("/users/**").hasRole("USER")  // User section requires USER role
//                        .anyRequest().authenticated()  // All other requests need to be authenticated
//                )
//                // Configure form login
//                .formLogin(form -> form
//                        .loginPage("/login")  // Custom login page
//                        .defaultSuccessUrl("/default")  // Redirect after successful login
//                        .permitAll()  // Allow everyone to see login page
//                )
//                // Configure logout
//                .logout(logout -> logout.permitAll());  // Allow everyone to logout
//
//        return http.build();
//    }
//
//}
