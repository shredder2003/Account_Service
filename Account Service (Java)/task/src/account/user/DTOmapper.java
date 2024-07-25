package account.user;

import account.admin.Role;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DTOmapper {

    private final ModelMapper modelMapper;

    public DTOmapper(@Autowired ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDTO convertUserToDTO(User user) {
        // here we make use of the 3rd party library to transform a User into a UserDTO
        log.info("convertUserToDTO user={}", user);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.id = user.getUser_id();
        userDTO.roles = user.getUserRoles().stream().map( Role::getFullName).sorted().toArray(String[]::new);
        userDTO.setEmail(userDTO.getEmail().toLowerCase() );
        log.info("convertUserToDTO userDTO.name={}", userDTO.getName());
        return userDTO;
    }

}
