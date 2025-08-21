package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.auth.AuthenticationRequest;
import com.ecommerce.e_commerce_api.dto.auth.AuthenticationResponse;
import com.ecommerce.e_commerce_api.dto.auth.RegisterRequest;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Role;
import com.ecommerce.e_commerce_api.repository.CustomerRepository;
import com.ecommerce.e_commerce_api.security.JwtService;
import com.ecommerce.e_commerce_api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email address is already in use.");
        }
        var customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .customerCode(UUID.randomUUID())
                .role(Role.USER)
                .build();

        var savedCustomer = customerRepository.save(customer);
        return buildAuthenticationResponse(savedCustomer);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User could not be found after authentication."));

        return buildAuthenticationResponse(customer);
    }

    public UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new IllegalStateException("A valid user session could not be found. Authorization may be required for this endpoint.");
        }

        return (UserPrincipal) authentication.getPrincipal();
    }

    public Long getAuthenticatedCustomerId() {
        return getCurrentUserPrincipal().getId();
    }

    public UUID getAuthenticatedCustomerCode() {
        return getCurrentUserPrincipal().getCustomerCode();
    }

    private AuthenticationResponse buildAuthenticationResponse(Customer customer) {
        UserDetails userDetails = UserPrincipal.create(customer);
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        long jwtExpiration = jwtService.getJwtExpiration();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .userId(customer.getId())
                .firstName(customer.getFirstName())
                .authorities(customer.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .build();
    }
}