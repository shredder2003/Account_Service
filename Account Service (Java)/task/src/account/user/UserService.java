package account.user;

import account.admin.AUTHORITY;
import account.admin.Role;
import account.admin.RoleRepository;
import account.admin.exception.RoleNotFoundException;
import account.user.exception.UserExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final DTOmapper dTOmapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;
    private final RoleRepository roleRepository;

    @Transactional
    public UserDTO signup(User user){
        log.info( "signup(+) email={} newpass={}",user.getEmail(),user.getPassword() );
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
        }else if(userRepository.findUserByUsernameIgnoreCase(user.getEmail().toLowerCase(Locale.ROOT)).isEmpty()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUsername( user.getEmail().toLowerCase(Locale.ROOT) );
            if(userRepository.count()>0){
                user.setUserRoles( new HashSet<> ( List.of(roleRepository.findRoleByCode(AUTHORITY.USER).orElseThrow(RoleNotFoundException::new) ) ) );
                log.info( "signup username={} added role: {}",user.getUsername(),AUTHORITY.USER );
            }else {
                Optional<Role> roleOptional = roleRepository.findRoleByCode(AUTHORITY.ADMINISTRATOR);
                Role role = roleOptional.orElseThrow(RoleNotFoundException::new);
                user.setUserRoles( new HashSet<> ( List.of(role) ) );
                log.info( "signup username={} added role: {}",user.getUsername(),AUTHORITY.ADMINISTRATOR );
            }
            userRepository.save(user);
            log.info( "signup(-) username={} newpass={}",user.getUsername(),user.getPassword() );
            return dTOmapper.convertUserToDTO(user);
        }else{
            throw new UserExistsException();
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public PasswordChanged changePassword(NewPassword newPassword){
        User user = currentUser.get().getUser();
        user = userRepository.findUserByUsernameIgnoreCase(user.getUsername()).get();
        log.info( "changePassword(+) username={} newpass={} user={}",user.getUsername(),newPassword.getNewPassword(), user );
        if( passwordEncoder.matches(newPassword.getNewPassword(), user.getPassword()) ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The passwords must be different!");
        }else {
            PasswordChanged passwordChanged = new PasswordChanged(user.getUsername(), "The password has been updated successfully");
            user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
            user = userRepository.save(user);
            log.info( "changePassword(-) username={} newpass={} user={}", user.getUsername(), newPassword.getNewPassword(), user );
            return passwordChanged;
        }
    }
}
