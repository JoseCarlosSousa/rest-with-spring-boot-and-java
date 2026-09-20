package pt.seixal.carlos.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pt.seixal.carlos.mapper.DozertMapper.parseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
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
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.file.exporter.contract.FileExporter;
import pt.seixal.carlos.file.exporter.factory.FileExporterFactory;
import pt.seixal.carlos.model.Permission;
import pt.seixal.carlos.model.User;
import pt.seixal.carlos.repository.PermissionRepository;
import pt.seixal.carlos.repository.UserRepository;
import pt.seixal.carlos.util.PageableUtils;
import pt.seixal.carlos.util.PagedModelUtils;

@Service
public class UserService implements UserDetailsService {

	private final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserRepository repository;

	@Autowired
	PermissionRepository permissionRepository;

	@Autowired
	FileExporterFactory exporter;

	@Autowired
	private ExportService exportService;

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

	public UserDTO create(UserDTO dto) {
		logger.info("Creating a new user");
		if (dto == null) {
			throw new RequiredObjectIsNullException();
		}

		return save(dto, null);
	}

	public UserDTO update(UserDTO dto) {
		logger.info("Edit User");

		if (dto == null) {
			throw new RequiredObjectIsNullException();
		}

		User entity = getUser(dto.getId());
		return save(dto, entity);
	}

	private UserDTO save(UserDTO dto, User entity) {
		if (dto == null) {
			throw new RequiredObjectIsNullException();
		}

		if (entity == null) {
			entity = new User();
			entity.setUserName(dto.getUserName());
			entity.setAccountNonExpired(true);
			entity.setAccountNonLocked(true);
			entity.setCredentialsNonExpired(true);
		}
		entity.setFullName(dto.getFullName());
		entity.setPassword(generateHashedPassword(dto.getPassword()));
		entity.setEnabled(dto.isEnabled());

		List<String> permissionNames = dto.getPermissions().stream()
				.map(Permission::getAuthority) // or .getDescription()
				.toList();
		List<Permission> bdPermissions = permissionRepository.findByDescriptionIn(permissionNames);
		entity.setPermissions(bdPermissions);

		var userDTO = parseObject(repository.save(entity), UserDTO.class);
		addHateoasLinks(userDTO);
		return userDTO;
	}

	public PagedModel<EntityModel<UserDTO>> findAll(Map<String, String> params) {
		logger.info("Finding all users!");

		Pageable pageable = PageableUtils.getPageable(params);
		return buildPageModel(params, repository.findAll(pageable));
	}

	public UserDTO findById(Long id) {
		logger.info("Finding one User!");
		var dto = parseObject(getUser(id), UserDTO.class);
		addHateoasLinks(dto);
		return dto;
	}

	public Resource exportPage(Map<String, String> params, String acceptHeader) {
		FileExporter exporter = this.exporter.getExporter(acceptHeader);
		return exportService.exportPage(
				params,
				acceptHeader,
				repository::findAll,
				UserDTO.class,
				exporter::exportUsers);
	}

	public Resource exportUser(Long id, String acceptHeader) {
		var dto = parseObject(getUser(id), UserDTO.class);
		FileExporter exporter = this.exporter.getExporter(acceptHeader);
		return exportService.exportSingle(dto, exporter::exportUser);
	}

	private User getUser(Long id) {
		logger.info("Getting User with id: {}", id);
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));
	}

	private String generateHashedPassword(String password) {
		PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put("pbkdf2", pbkdf2Encoder);

		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
		return passwordEncoder.encode(password);
	}

	private PagedModel<EntityModel<UserDTO>> buildPageModel(Map<String, String> params, Page<User> users) {
		Link findAllLink = linkTo(methodOn(UserController.class).findAll(params)).withSelfRel();

		return PagedModelUtils.buildPageModel(
				params,
				users,
				UserDTO.class,
				assembler,
				findAllLink,
				this::addHateoasLinks);
	}

	private void addHateoasLinks(UserDTO dto) {
		addHateoasLinks(dto, null);
	}

	private void addHateoasLinks(UserDTO dto, Map<String, String> params) {
		dto.add(linkTo(methodOn(UserController.class).findById(dto.getId())).withSelfRel().withType("GET"));
		dto.add(linkTo(methodOn(UserController.class).findAll(params)).withRel("findAll").withType("GET"));
		dto.add(linkTo(methodOn(UserController.class).create(dto)).withRel("create").withType("POST"));
		dto.add(linkTo(methodOn(UserController.class).update(dto)).withRel("update").withType("PUT"));
		dto.add(linkTo(methodOn(UserController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
	}
}
