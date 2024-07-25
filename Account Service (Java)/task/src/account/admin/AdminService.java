package account.admin;

import account.admin.exception.*;
import account.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DTOmapper dtOmapper;
    @Autowired
    private final CurrentUser currentUser;

    public void checkIsAdmin(){
        Role roleAdmin = roleRepository.findRoleByCode(AUTHORITY.ADMINISTRATOR).orElseThrow();
        User reReadUser = userRepository.findUserByUsernameIgnoreCase(currentUser.get().getUser().getUsername()).orElseThrow();
        if( ! reReadUser.getUserRoles().contains(roleAdmin) ) {
            throw new AccessDeniedException();
        }
    }

    public UserStatusDTO deleteUser(String username){
        checkIsAdmin();
        User deletedUser = userRepository.findUserByUsernameIgnoreCase(username).orElseThrow(UserNotFoundException::new);
        Role adminRole = roleRepository.findRoleByCode(AUTHORITY.ADMINISTRATOR).orElseThrow();
        if(deletedUser.getUserRoles().contains(adminRole)){
            throw new CanNotDeleteAdminUser();
        }
        UserStatusDTO userStatusDTO = new UserStatusDTO(deletedUser.getUsername(),"Deleted successfully!");
        userRepository.delete(deletedUser);
        return userStatusDTO;
    }

    public List<UserDTO> listUser(){
        checkIsAdmin();
        return userRepository.findAll().stream()
                .map(dtOmapper::convertUserToDTO)
                .sorted(Comparator.comparingLong(UserDTO::getId))
                .collect(Collectors.toList());
    }

    public UserDTO processRoleAction(RoleAction roleAction){
        log.info("processRoleAction(+) RoleAction {}", roleAction);
        checkIsAdmin();
        AUTHORITY authority = AUTHORITY.forName(roleAction.getRole());
        if(authority == null){
            throw new RoleNotFoundException();
        }
        String GRANT = "GRANT";
        String REMOVE = "REMOVE";
        if(roleAction.getOperation().equals(GRANT)){
            return addRole(roleAction.getUser().toLowerCase(), AUTHORITY.valueOf(roleAction.getRole()) );
        } else if(roleAction.getOperation().equals(REMOVE)){
            return removeRole(roleAction.getUser().toLowerCase(), AUTHORITY.valueOf(roleAction.getRole()) );
        }else{
            throw new RuntimeException("roleAction should be in {%s,%s}, but found %s".formatted(GRANT, REMOVE,roleAction.getOperation()));
        }
    }

    public UserDTO removeRole(String username, AUTHORITY authority){
        checkIsAdmin();
        if(authority.equals(AUTHORITY.ADMINISTRATOR)){
            throw new CanNotDeleteAdminRole();
        }
        User user = userRepository.findUserByUsernameIgnoreCase(username).orElseThrow(UserNotFoundException::new);
        Role role = roleRepository.findRoleByCode(authority).orElseThrow(RoleNotFoundException::new);
        Set<Role> roleList = user.getUserRoles();
        if(! roleList.contains(role)){
            throw new UserHaveNotRole();
        }
        if(roleList.size()==1){
            throw new UserMustHaveOneRole();
        }
        roleList.remove(role);
        user.setUserRoles(roleList);
        userRepository.save(user);
        return dtOmapper.convertUserToDTO(user);
    }

    public UserDTO addRole(String username, AUTHORITY authority){
        log.info("addRole(+) username {} authority {}", username, authority);
        checkIsAdmin();
        User user = userRepository.findUserByUsernameIgnoreCase(username).orElseThrow(UserNotFoundException::new);
        Role role = roleRepository.findRoleByCode(authority).orElseThrow(RoleNotFoundException::new);
        Set<Role> roleList = user.getUserRoles();
        if( roleList.stream().anyMatch(groupI -> ! groupI.getCode().getRoleType().equals(role.getCode().getRoleType())) ){
            throw new AdminAndBusinessRolesToOneUser();
        }
        roleList.add(role);
        user.setUserRoles(roleList);
        userRepository.save(user);
        log.info("addRole(-) username {} authority {}", username, authority);
        return dtOmapper.convertUserToDTO(user);
    }

}
