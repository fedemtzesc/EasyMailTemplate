package com.fdxsoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fdxsoft.entities.UserEntity;
import com.fdxsoft.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository repository;
	
	
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

}
