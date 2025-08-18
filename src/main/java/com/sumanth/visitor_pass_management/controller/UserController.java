package com.sumanth.visitor_pass_management.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sumanth.visitor_pass_management.entity.MyUser;
import com.sumanth.visitor_pass_management.helper.AuthRequest;
import com.sumanth.visitor_pass_management.helper.MyToken;
import com.sumanth.visitor_pass_management.service.JwtService;
import com.sumanth.visitor_pass_management.service.MyUserDetailsService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
 
@RestController
public class UserController {
	@Autowired
	private MyUserDetailsService us;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;
 
	@GetMapping("/v1/home")
	public String home() {
		return "Everybody is welcome";
	}
 
	@GetMapping("/v2/employee/home")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@SecurityRequirement(name = "Bearer Authentication")
	public String userHome(Authentication authentication) {
		String username=authentication.getName();
		System.out.println(username);
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		for(GrantedAuthority role:roles)
		{
			System.out.println(role.getAuthority());
		}
		return "Employee home page";
	}
 
	@GetMapping("/v2/security head/home")
	@PreAuthorize("hasAuthority('SECURITY HEAD')")
	@SecurityRequirement(name = "Bearer Authentication")
	public String adminHome(Authentication authentication) {
		String username=authentication.getName();
		System.out.println(username);
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		for(GrantedAuthority role:roles)
		{
			System.out.println(role.getAuthority());
		}
		return "Hi Security Head, welcome";
	}
 
	@PostMapping("/v1/signup")
	public MyUser signup(@RequestBody MyUser myUser) {
 
//		return us.addNewUser(myUser);
		try {
            return us.addNewUser(myUser);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw e;
            }
            throw new UsernameNotFoundException("Signup failed");
        }
	}
 
	@PostMapping("/v1/login")
	public MyToken login(@RequestBody AuthRequest authRequest)
	{
		MyToken token=new MyToken();
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		if(auth.isAuthenticated())
		{
			String jwt=jwtService.generateToken(authRequest.getUsername());
			token.setUsername(authRequest.getUsername());
			token.setToken(jwt);
			token.setAuthorities(auth.getAuthorities());
		}else
		{
			throw new UsernameNotFoundException("Login failed");
		}
		return token;
	}
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleConflict(ResponseStatusException e) {
        return new ResponseEntity<>(e.getReason(), e.getStatusCode());
    }
}
 
 
