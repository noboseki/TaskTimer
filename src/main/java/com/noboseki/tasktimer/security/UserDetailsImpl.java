package com.noboseki.tasktimer.security;

import com.noboseki.tasktimer.domain.Authority;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Transient;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private String username;
    private String password;
    private Boolean enabled;
    private Set<Authority> authorities;
    @Builder.Default
    private Boolean accountNonExpired = true;
    @Builder.Default
    private Boolean accountNonLocked = true;
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                .collect(Collectors.toSet());
    }
}
