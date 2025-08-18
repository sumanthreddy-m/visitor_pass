package com.sumanth.visitor_pass_management.helper;
 
import java.util.Collection;
 
import org.springframework.security.core.GrantedAuthority;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
 
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class MyToken {
	private String token;
	private String username;
	private Collection<? extends GrantedAuthority> authorities;
}
 
