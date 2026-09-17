package pt.seixal.carlos.services;

import static pt.seixal.carlos.mapper.ObjectMapper.parseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
import org.springframework.stereotype.Service;

import pt.seixal.carlos.controllers.UserController;
import pt.seixal.carlos.data.dto.v1.UserDTO;
import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.model.Permission;
import pt.seixal.carlos.model.User;
import pt.seixal.carlos.repository.PermissionRepository;
import pt.seixal.carlos.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserRepository repository;

	@Autowired
	PermissionRepository permissionRepository;

	@Autowired
	PagedResourcesAssembler<UserDTO> assembler;

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

	public UserDTO create(AccountCredentialsDTO user) {
		logger.info("Creating a new user");
		if (user == null) {
			throw new RequiredObjectIsNullException();
		}

		var entity = new User();
		entity.setFullName(user.getFullName());
		entity.setUserName(user.getUsername());
		entity.setPassword(generateHashedPassword(user.getPassword()));
		entity.setAccountNonExpired(true);
		entity.setAccountNonLocked(true);
		entity.setCredentialsNonExpired(true);
		entity.setEnabled(true);
		if (user.getRole() != null) {
			List<Permission> persistedPermissions = new ArrayList<>();
			Permission persisted = permissionRepository.findByDescription(user.getRole())
					.orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + user.getRole()));
			persistedPermissions.add(persisted);
			entity.setPermissions(persistedPermissions);
		}

		return parseObject(repository.save(entity), UserDTO.class);
	}

	private String generateHashedPassword(String password) {
		PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put("pbkdf2", pbkdf2Encoder);

		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
		return passwordEncoder.encode(password);
	}

	public PagedModel<EntityModel<UserDTO>> findAll(Pageable pageable) {
		logger.info("Finding all users!");

		var users = repository.findAll(pageable);

		var peopleWithLinks = users.map(user -> {
			var dto = parseObject(user, UserDTO.class);
			return dto;
		});

		Link findAllLink = WebMvcLinkBuilder.linkTo(
				WebMvcLinkBuilder.methodOn(UserController.class)
						.findAll(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().toString()))
				.withSelfRel();

		return assembler.toModel(peopleWithLinks, findAllLink);
	}

}
