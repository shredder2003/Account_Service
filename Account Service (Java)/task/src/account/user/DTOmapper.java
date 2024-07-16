package account.user;

import account.admin.Group;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DTOmapper {

    private final ModelMapper modelMapper;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public DTOmapper(@Autowired ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDTO convertUserToDTO(User user) {
        // here we make use of the 3rd party library to transform a User into a UserDTO
        logger.info("convertUserToDTO user="+user);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.id = user.getUser_id();
        userDTO.roles = user.getUserGroups().stream().map( Group::getRoleName ).toArray(String[]::new);
        logger.info("convertUserToDTO userDTO.name="+userDTO.getName());
        return userDTO;
    }

}
