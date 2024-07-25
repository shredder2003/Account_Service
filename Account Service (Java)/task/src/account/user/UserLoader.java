package account.user;

import account.admin.AUTHORITY;
import account.admin.Role;
import account.admin.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class UserLoader {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserLoader(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        if(roleRepository.count()==0) {
            createRoles();
        }
        /*if(userRepository.count()==0) {
            createUsers();
        }*/
    }


    private void createRoles() {
        log.info("createRoles(+)");
        Role admin = roleRepository.save(new Role(AUTHORITY.ADMINISTRATOR));
        log.info("createRoles admin={}", admin);
        Role accountant = roleRepository.save(new Role(AUTHORITY.ACCOUNTANT));
        log.info("createRoles accountant={}", accountant);
        Role user = roleRepository.save(new Role(AUTHORITY.USER));
        log.info("createRoles user={}", user);
        log.info("createRoles(-)");
    }

    private void createUsers() {
        log.info("createUsers(+)");
        User user = new User("admin", "name", "admin@acme.com","123456789101112");
        user.setUsername(user.getEmail().toLowerCase());
        Role admin = roleRepository.findRoleByCode(AUTHORITY.ADMINISTRATOR).orElseThrow();
        log.info("createUsers find admin={}", admin);
        user.setUserRoles( Set.of(admin) );
        log.info("createUsers before save admin={}", admin);
        userRepository.save(user);
        log.info("createUsers(-)");
    }


}
