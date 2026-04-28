package com.fdxsoft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fdxsoft.config.filters.JwtTokenValidator;
import com.fdxsoft.service.impl.UserDetailsServiceImpl;
import com.fdxsoft.utils.JwtUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtUtils jwtUtils;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

	    return httpSecurity
	        .csrf(csrf -> csrf.disable())
	        .httpBasic(httpBasic -> httpBasic.disable())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(http -> {
	        	// End-points publicos y de libre acceso
	        	http.requestMatchers(HttpMethod.GET, "/").permitAll();
	        	http.requestMatchers(HttpMethod.GET, "/index").permitAll();
	        	http.requestMatchers(HttpMethod.GET, "/catalog/**").permitAll();
	        	http.requestMatchers(HttpMethod.GET, "/css/**").permitAll();
	        	http.requestMatchers(HttpMethod.GET, "/emails/**").permitAll();
	        	http.requestMatchers(HttpMethod.GET, "/img/**").permitAll();
	        	http.requestMatchers(HttpMethod.GET, "/js/**").permitAll();
	        	http.requestMatchers(HttpMethod.POST, "/auth/v1/**").permitAll();
	        	
	        	// End-points con restricciones
	        	
	        	// TESTING
	        	http.requestMatchers(HttpMethod.GET, "/auth/get")
	        						.access(new WebExpressionAuthorizationManager("hasRole('USER') or (hasRole('ADMIN') and hasAuthority('CONFIG'))"));
	        	http.requestMatchers(HttpMethod.POST, "/auth/post").hasRole("USER");
	        	http.requestMatchers(HttpMethod.PUT, "/auth/put").hasRole("MANAGER");
	        	http.requestMatchers(HttpMethod.PATCH, "/auth/patch").hasRole("OPERATOR");
	        	http.requestMatchers(HttpMethod.DELETE, "/auth/delete").hasRole("GUEST");
	        	//http.requestMatchers(HttpMethod.GET, "/auth/get").hasAuthority("CONFIG");
	        	
	        	// Cualquier otro, se le niega el acceso si no esta especificado
	        	http.anyRequest().authenticated();
	        })
	        .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
	        .build();
	}

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 
     * @param passwordEncoder
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService,
    		PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}