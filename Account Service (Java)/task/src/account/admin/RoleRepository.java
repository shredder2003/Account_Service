package account.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Transactional(readOnly = true)
    Optional<Role> findRoleByCode(AUTHORITY code);
}
