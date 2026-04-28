package com.fdxsoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fdxsoft.controllers.dtos.AuthCreateUserRequestDTO;
import com.fdxsoft.controllers.dtos.AuthLoginRequest;
import com.fdxsoft.controllers.dtos.AuthResponse;
import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.entities.RoleEntity;
import com.fdxsoft.entities.UserEntity;
import com.fdxsoft.exceptions.InvalidUserRequestException;
import com.fdxsoft.exceptions.UserAlreadyExistsException;
import com.fdxsoft.repositories.RoleRepository;
import com.fdxsoft.repositories.UserRepository;
import com.fdxsoft.utils.JwtUtils;

import jakarta.validation.Valid;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Value("${spring.security.max.roles.allowed.for.user}")
	int roleQty;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserEntity userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

		List<SimpleGrantedAuthority> authorityList = new ArrayList<SimpleGrantedAuthority>();

		// Tenemos que traducir cada rol a un SimpleGrantedAuthority que es lo que
		// entiende SS y meterlos a la lista
		userEntity.getRoles()
				.forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getRole().name())));

		// Ahora metemos cada uno de los permisos de los roles dentro del autorityList
		// pero sin el prefijo ROLE_
		userEntity.getRoles().stream().flatMap(role -> role.getPermissions().stream())
				.forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

		return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.isEnabled(),
				userEntity.isAccountNonExpired(), userEntity.isCredentialsNonExpired(), userEntity.isAccountNonLocked(),
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
			AuthResponse authResponse = new AuthResponse(username, accessToken);

			response.setMessage("Autenticacion exitosa.");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.OK.value());
			response.setData(List.of(authResponse));
		} catch (UsernameNotFoundException e) {
			response.setMessage("ERROR: " + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.UNAUTHORIZED.value());
		} catch (BadCredentialsException e) {
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

		if (userDetails == null) {
			throw new BadCredentialsException("El usuario es invalido.");
		}

		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("El password es invalido.");
		}

		return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(),
				userDetails.getAuthorities());
	}

	public GenericResponseDTO<AuthResponse> createUser(@Valid AuthCreateUserRequestDTO userToCreate) {
		GenericResponseDTO<AuthResponse> response = new GenericResponseDTO<>();
		
		try {
			String username = userToCreate.username();
			
			Optional<UserEntity> findUserEntity = userRepository.findByUsername(username);
			if(findUserEntity.isPresent())
				throw new UserAlreadyExistsException("El usuario " + username + " ya esta registrado. Elija otro por favor.");
			
			List<String> roleRequest = userToCreate.roleRequest().roleListName();
			Set<RoleEntity> roleEntitySet;
			
			if(roleRequest.isEmpty())
				throw new InvalidUserRequestException("Tiene que especificar al menos un rol para el usuario.");
			else if(roleRequest.size()>roleQty)
				throw new InvalidUserRequestException("No se permite tener mas de " + roleQty + " roles por usuario.");
			
			try {
				roleEntitySet = roleRepository.findByRoleIn(roleRequest).stream()
						.collect(Collectors.toSet());						
			} catch (Exception e) {
				throw new InvalidUserRequestException("Al menos uno de los roles que especifico es invalido.");
			}
			
			
			UserEntity newUserEntity = UserEntity.builder().username(userToCreate.username())
					.password(passwordEncoder.encode(userToCreate.password())).name(userToCreate.name())
					.lastName(userToCreate.lastName()).phone(userToCreate.phone()).email(userToCreate.email())
					.roles(roleEntitySet).enabled(true).accountNonLocked(true).accountNonExpired(true)
					.credentialsNonExpired(true).build();
			UserEntity userCreated = userRepository.save(newUserEntity);

			List<SimpleGrantedAuthority> authorityList = new ArrayList<SimpleGrantedAuthority>();
			userCreated.getRoles()
					.forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getRole().name())));
			userCreated.getRoles().stream().flatMap(role -> role.getPermissions().stream())
					.forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

			
			Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(),
					userCreated.getPassword(), authorityList);
			String accessToken = jwtUtils.createToken(authentication);
			AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), accessToken);

			response.setMessage("Usuario creado con exito.");
			response.setStatus("success");
			response.setHttpStatus(HttpStatus.CREATED.value());
			response.setData(List.of(authResponse));
		} catch(UserAlreadyExistsException e) {
			response.setMessage("" + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.CONFLICT.value());
		} catch(InvalidUserRequestException e) {
			response.setMessage("" + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.BAD_REQUEST.value());
		} catch (Exception e) {
			response.setMessage("" + e.getMessage());
			response.setStatus("error");
			response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

}
