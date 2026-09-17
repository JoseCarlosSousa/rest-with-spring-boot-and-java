package pt.seixal.carlos.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.model.User;
import pt.seixal.carlos.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserRepository repository;

	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = repository.findByUsername(username);
		if (user != null) {
			return user;
		} else {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}
	}

	public void delete(Long id) {
		logger.info("delete Person");

		User entity = getUser(id);

		repository.delete(entity);
	}

	private User getUser(Long id) {
		logger.info("Getting User with id: {}", id);
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));
	}

}
