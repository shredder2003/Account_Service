package account.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    //@Query("SELECT u FROM USER u JOIN FETCH u.group WHERE u.username = :username")
    Optional<User> findUserByUsernameIgnoreCase(String username);

}