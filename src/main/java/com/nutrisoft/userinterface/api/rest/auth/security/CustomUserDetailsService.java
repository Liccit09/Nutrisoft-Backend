package com.nutrisoft.userinterface.api.rest.auth.security;

import com.nutrisoft.core.port.out.auth.CredentialRepository;
import com.nutrisoft.core.shared.component.common.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService implementation.
 * Loads user details from the database using AuthRepositoryPort.
 * This service adapts credentials to UserDetails for Spring Security.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CredentialRepository credentialRepository;

    /**
     * Load user details by email (username).
     * This is called by Spring Security during authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user details for email: {}", email);
        try {
            Email emailVO = Email.of(email);
            var credential = credentialRepository.findByEmail(emailVO)
                    .orElseThrow(() -> new UsernameNotFoundException("Credential not found for email: " + email));
            
            log.debug("Credential found for email: {}", email);
            return UserPrincipal.createFromCredential(credential);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error loading user details for email: {}", email, e);
            throw new UsernameNotFoundException("Error loading user details: " + e.getMessage(), e);
        }
    }
}
