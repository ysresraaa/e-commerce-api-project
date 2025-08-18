package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.auth.AuthenticationRequest;
import com.ecommerce.e_commerce_api.dto.auth.AuthenticationResponse;
import com.ecommerce.e_commerce_api.dto.auth.RegisterRequest;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Role; // Role enum'ını import edin
import com.ecommerce.e_commerce_api.repository.ICustomerRepository;
import com.ecommerce.e_commerce_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ICustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        var customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();


        if(customerRepository.findByEmail(customer.getEmail()).isPresent()){
            throw new IllegalStateException("This email address is already in use.");
        }

        customerRepository.save(customer);
        var jwtToken = jwtService.generateToken(customer);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
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

        var jwtToken = jwtService.generateToken(customer);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}