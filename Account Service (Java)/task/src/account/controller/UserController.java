package account.controller;

import account.*;
import account.dto.UserDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@RestController()
@RequestMapping("/api/auth")
public class UserController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DTOmapper dTOmapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserController(DTOmapper dTOmapper, UserRepository repository, PasswordEncoder passwordEncoder) {
        this.dTOmapper = dTOmapper;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        //repository.deleteAll();
    }

    @PostMapping("signup")
    public ResponseEntity<UserDTO> postAuthSignUp(@RequestBody @Valid account.User user) {
        //public UserNoPass postAuthSignUp(@RequestBody User user) {
        logger.info("user="+user);
        //return new UserNoPass(user.name(), user.lastname(), user.email()) ;
        /*if(user.getEmail().endsWith("@acme.com")
                && ( user.getName() != null && ! user.getName().isEmpty() )
                && ( user.getLastname() != null && ! user.getLastname().isEmpty() )
                && ( user.getEmail() != null && ! user.getEmail().isEmpty() )
                && ( user.getPassword() != null && ! user.getPassword().isEmpty() )
        ) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            repository.save(user);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body( dTOmapper.convertUserToDTO(user) );
        }else{
            return ResponseEntity.badRequest().build();
            //return new ResponseEntity(new ApiErrors(errors), HttpStatus.BAD_REQUEST);
            //ResponseEntity(new ApiErrors(errors), HttpStatus.BAD_REQUEST);
        }*/
        if(user.getName()==null||user.getName().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name is null!");
        }else if (user.getLastname()==null||user.getLastname().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Last name is null!");
        }else if (user.getEmail()==null||user.getEmail().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email is null!");
        /* }else if (user.getPassword()==null||user.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password is null!");
        }else if( BreachedPasswords.inBreachedList(user.getPassword()) ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The password is in the hacker's database!");*/
        }else if(! user.getEmail().endsWith("@acme.com")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Wrong email!");
        }else if(repository.findUserByUsername(user.getEmail().toLowerCase(Locale.ROOT)).isEmpty()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUsername( user.getEmail().toLowerCase(Locale.ROOT) );
            user.setAuthority("ROLE_USER");
            repository.save(user);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body( dTOmapper.convertUserToDTO(user) );
        }else{
            //return ResponseEntity.badRequest().build();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User exist!");
        }
    }

    @PostMapping("changepass")
    public ResponseEntity<PasswordChanged> postAuthChangePass(@Valid @RequestBody NewPassword newPassword, Authentication auth) {
        if(auth==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else if(newPassword==null
                || newPassword.getNewPassword().isEmpty()
                || newPassword.getNewPassword().isBlank()
                || newPassword.getNewPassword().length()<12
        )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password length must be 12 chars minimum!");
        }else {
            UserDetails details = (UserDetails) auth.getPrincipal();
            User user = repository.findUserByUsername(details.getUsername()).get();
            if( passwordEncoder.matches(newPassword.getNewPassword(), user.getPassword()) ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The passwords must be different!");
            /* }else if( BreachedPasswords.inBreachedList(newPassword.getNew_password()) ){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The password is in the hacker's database!");*/
            }else {
                PasswordChanged passwordChanged = new PasswordChanged(user.getUsername(), "The password has been updated successfully");
                user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
                repository.save(user);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(passwordChanged);
            }
        }
    }

}
