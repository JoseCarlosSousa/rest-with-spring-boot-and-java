package pt.seixal.carlos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.seixal.carlos.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Optional<Permission> findByDescription(String description);

	List<Permission> findByDescriptionIn(List<String> descriptions);

}
