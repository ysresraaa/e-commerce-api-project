package com.ecommerce.e_commerce_api.security;

import com.ecommerce.e_commerce_api.model.Customer;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final UUID customerCode;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, UUID customerCode, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.customerCode = customerCode;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Customer customer) {
        return new UserPrincipal(
                customer.getId(),
                customer.getCustomerCode(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAuthorities()
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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