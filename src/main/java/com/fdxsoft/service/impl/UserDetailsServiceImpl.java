package com.fdxsoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fdxsoft.controllers.dtos.AuthLoginRequest;
import com.fdxsoft.controllers.dtos.AuthResponse;
import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.entities.UserEntity;
import com.fdxsoft.entities.WYSIWYGEntity;
import com.fdxsoft.repositories.UserRepository;
import com.fdxsoft.utils.JwtUtils;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository repository;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserEntity userEntity = repository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("El usuario " + username + " no existe."));
		
		List<SimpleGrantedAuthority> authorityList = new ArrayList<SimpleGrantedAuthority>();
		
		//Tenemos que traducir cada rol a un SimpleGrantedAuthority que es lo que entiende SS y meterlos a la lista
		userEntity.getRoles()
			.forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getROLE().name())));
		 
		//Ahora metemos cada uno de los permisos de los roles dentro del autorityList pero sin el prefijo ROLE_
		userEntity.getRoles().stream()
			.flatMap(role -> role.getPermissions().stream())
			.forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));
		
		return new User(userEntity.getUsername(),
				userEntity.getPassword(),
				userEntity.isEnabled(),
				userEntity.isAccountNonExpired(),
				userEntity.isCredentialsNonExpired(),
				userEntity.isAccountNonLocked(),
				authorityList);
	}
	
	public GenericResponseDTO<AuthResponse> loginUser(AuthLoginRequest authLoginRequest) {
		GenericResponseDTO<AuthResponse> response = new GenericResponseDTO<>();
		
		try {
			String username = authLoginRequest.username();
			String password = authLoginRequest.password();
			
			Authentication authentication = this.authenticate(username, password);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String accessToken = jwtUtils.createToken(authentication);
			AuthResponse authResponse=  new AuthResponse(username, accessToken);
			
			response.setMessage("Autenticacion exitosa.");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
			response.setData(List.of(authResponse));
		} catch(UsernameNotFoundException e) {
			response.setMessage("ERROR: " + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.UNAUTHORIZED.value());
		} catch(BadCredentialsException e) {
			response.setMessage("ERROR: " + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.UNAUTHORIZED.value());		
		} catch (Exception e) {
			response.setMessage("ERROR: " + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return response;		
	}
	
	private Authentication authenticate(String username, String password) {
		UserDetails userDetails = this.loadUserByUsername(username);
		
		if(userDetails == null) {
			throw new BadCredentialsException("El usuario es invalido.");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("El password es invalido.");
		}
		
		return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
	}
	

}
