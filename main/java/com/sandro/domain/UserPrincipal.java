package com.sandro.domain;

import com.sandro.dto.UserDTO;
import com.sandro.dtomapper.UserDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 01.07.2023
 */

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return
//                Arrays.stream(this.role.getPermissions().split(",".trim()))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
                AuthorityUtils.commaSeparatedStringToAuthorityList(this.role.getPermissions());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }

    public UserDTO getUser() {
        return UserDTOMapper.fromUser(this.user, role);
    }

}
