package pt.seixal.carlos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.seixal.carlos.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Optional<Permission> findByDescription(String description);
}
