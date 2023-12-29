package com.example.projetsantesecurise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService getDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider getAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(getDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeHttpRequests()
				.requestMatchers("/", "/signup", "/signin", "/forgot-password","/forgotPassword","/reset-password","/newpassword","/users").permitAll()
				// Permit access to /admin only for users with the role ADMIN
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/signin").loginProcessingUrl("/userlogin")
				.defaultSuccessUrl("/")
				.successHandler((request, response, authentication) -> {
					for (GrantedAuthority auth : authentication.getAuthorities()) {
						if (auth.getAuthority().equals("ROLE_PROF")) {
							response.sendRedirect("/prof");
						} else if (auth.getAuthority().equals("ROLE_ADMIN")) {
							response.sendRedirect("/admin");
						}
					}
				})
				.permitAll();

		return http.build();
	}

}
