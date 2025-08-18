package com.sumanth.visitor_pass_management.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sumanth.visitor_pass_management.entity.MyUser;
import com.sumanth.visitor_pass_management.repository.MyUserRepository;
 
@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MyUserRepository ur; // Ensure this is properly defined
 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> temp = ur.findById(username);
        if (temp.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        MyUser myUser = temp.get();
 
        String str = myUser.getRoles();
        String[] roles = str.split(",");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return new User(myUser.getUsername(), myUser.getPassword(), authorities);
    }
 
    public MyUser addNewUser(MyUser myUser) {
        if (ur.existsById(myUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        String plainPassword = myUser.getPassword();
        String encPassword = passwordEncoder.encode(plainPassword);
        myUser.setPassword(encPassword);
        return ur.save(myUser);
    }
}