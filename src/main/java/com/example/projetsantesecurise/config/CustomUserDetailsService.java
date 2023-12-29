package com.example.projetsantesecurise.config;

import com.example.projetsantesecurise.models.User;
import com.example.projetsantesecurise.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UtilisateurRepository userrepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userrepo.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("user name not found");
		} else {
			return new CustomUser(user);
		}
	}

}