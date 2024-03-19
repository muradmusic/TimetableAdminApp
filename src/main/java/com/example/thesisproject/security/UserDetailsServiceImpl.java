package com.example.thesisproject.security;

import com.example.thesisproject.datamodel.entity.Role;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.service.RoleService;
import com.example.thesisproject.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {


    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }





    @Override
    public UserDetails loadUserByUsername(String username)  {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            logger.error("User Not Found with username: {}", username);
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }

        // Direct logging before getRoles()


        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        user.getRoles().forEach(role -> {
//            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
//            authorities.add(authority);
//            logger.debug("Granted Authority: {}", authority.getAuthority());
//        });
        List<Role> roles = roleService.getRolesByUserId(user.getId());
        roles.forEach(role -> {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
            authorities.add(authority);
            logger.debug("Granted Authority: {}", authority.getAuthority());
        });


        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        logger.info("UserDetails loaded for User: {}", username);
        logger.debug("Loading roles for user: {}. Roles is null? {}, Roles count: {}",
                user.getUsername(),
                user.getRoles() == null ? "yes" : "no",
                user.getRoles() != null ? user.getRoles().size() : "N/A");

        return userDetails;
    }


//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findUserByUsername(username);
//        if (user == null) throw new UsernameNotFoundException("User Not Found");
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        user.getRoles().forEach(role -> {
//            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
//            authorities.add(authority);
//        });
//        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
//        return userDetails;
//    }

//@Override
//public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    User user = userRepository.findUserByUsername(username);
//
//    List<GrantedAuthority> authorities = user.getRoles().stream()
//            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
//            .collect(Collectors.toList());
//
//    return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
//}



}
