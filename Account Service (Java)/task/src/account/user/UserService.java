package account.user;

import account.admin.AUTHORITY;
import account.admin.Group;
import account.admin.GroupRepository;
import account.admin.exception.RoleNotFoundException;
import account.user.exception.UserExistsException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final DTOmapper dTOmapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;
    private final GroupRepository groupRepository;

    public UserDTO signup(User user){
        if(user.getName()==null||user.getName().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name is null!");
        }else if (user.getLastname()==null||user.getLastname().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Last name is null!");
        }else if (user.getEmail()==null||user.getEmail().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email is null!");
        }else if(! user.getEmail().endsWith("@acme.com")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Wrong email!");
        }else if(repository.findUserByUsername(user.getEmail().toLowerCase(Locale.ROOT)).isEmpty()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUsername( user.getEmail().toLowerCase(Locale.ROOT) );
            if(repository.count()>0){
                user.setUserGroups( new HashSet<> ( List.of(groupRepository.findGroupByCode(AUTHORITY.USER).orElseThrow(RoleNotFoundException::new) ) ) );
            }else {
                Optional<Group> roleOptional = groupRepository.findGroupByCode(AUTHORITY.ADMINISTRATOR);
                Group role = roleOptional.orElseThrow(RoleNotFoundException::new);
                user.setUserGroups( new HashSet<> ( List.of(role) ) );
            }
            repository.save(user);
            return dTOmapper.convertUserToDTO(user);
        }else{
            throw new UserExistsException();
        }
    }

    @PreAuthorize("isAuthenticated()")
    public PasswordChanged changePassword(NewPassword newPassword){
        User user = currentUser.get().getUser();
        if( passwordEncoder.matches(newPassword.getNewPassword(), user.getPassword()) ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The passwords must be different!");
        }else {
            PasswordChanged passwordChanged = new PasswordChanged(user.getUsername(), "The password has been updated successfully");
            user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
            repository.save(user);
            return passwordChanged;
        }
    }
}
