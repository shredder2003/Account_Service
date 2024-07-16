package account.admin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GroupLoader {

    private GroupRepository groupRepository;
    private Group administator;
    private Group user;
    private Group accountant;

    @Autowired
    public GroupLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            administator = groupRepository.save(new Group(AUTHORITY.ADMINISTRATOR));
            accountant = groupRepository.save(new Group(AUTHORITY.ACCOUNTANT));
            user = groupRepository.save(new Group(AUTHORITY.USER));
        } catch (Exception e) {

        }
    }
}