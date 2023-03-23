package ru.skypro.homework.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.SecurityUserDto;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
public class SecurityUser implements UserDetails {

    private SecurityUserDto securityUserDto;


    @Override
    public String getUsername() {
        return securityUserDto.getEmail();
    }

    @Override
    public String getPassword() {
        return securityUserDto.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(
                new SimpleGrantedAuthority("ROLE_" + securityUserDto.getRole().name())
        );
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
