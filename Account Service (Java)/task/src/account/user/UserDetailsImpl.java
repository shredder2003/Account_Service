package account.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final User user;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        User reReadUser = userRepository.findUserByUsernameIgnoreCase(user.getUsername()).get();
        return reReadUser.getUserRoles().stream().map(n-> new SimpleGrantedAuthority(n.getFullName())).toList();
        //return user.getUserGroups().stream().map(n-> new SimpleGrantedAuthority(n.getRoleName())).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
