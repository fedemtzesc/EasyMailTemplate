package com.fdxsoft.config;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

	    return httpSecurity
	        .csrf(csrf -> csrf.disable())
	        .httpBasic(Customizer.withDefaults())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(http -> {
	        	// End-points publicos y de libre acceso
	        	http.requestMatchers(HttpMethod.GET, "/auth/hello").permitAll();
	        	
	        	// End-points con restricciones
	        	http.requestMatchers(HttpMethod.GET, "/auth/hello-secured").hasAuthority("CREATE");
	        	
	        	// Cualquier otro, se le niega el acceso si no esta especificado
	        	http.anyRequest().denyAll();
	        })
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
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsServiceList());
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
    	UserDetails userDetails = User.withUsername("federico")
    			.password("1234")
    			.roles("ADMIN")
    			.authorities("READ", "CREATE")
    			.build();
    	
    	return new InMemoryUserDetailsManager(userDetails);
    }
    
    @Bean
    public UserDetailsService userDetailsServiceList() {
    	List<UserDetails> userDetailsList = new ArrayList<UserDetails>();
    	userDetailsList.add(User.withUsername("federico")
    			.password("12345")
    			.roles("ADMIN")
    			.authorities("READ", "CREATE")
    			.build());
    	userDetailsList.add(User.withUsername("yolanda")
    			.password("12345")
    			.roles("USER")
    			.authorities("READ")
    			.build());
    	    	
    	return new InMemoryUserDetailsManager(userDetailsList);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}