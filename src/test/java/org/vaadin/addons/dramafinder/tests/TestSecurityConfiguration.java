package org.vaadin.addons.dramafinder.tests;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security for the test server (issue #216): every view stays open, as
 * the other ITs expect, except {@code security/admin}, which needs the
 * {@code ADMIN} role. Vaadin protects its own requests against CSRF.
 */
@Configuration(proxyBeanMethods = false)
public class TestSecurityConfiguration {

    @Bean
    SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/security/admin").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .build();
    }

    /** The user behind {@code @WithUserDetails("bob")}. */
    @Bean
    UserDetailsService testUsers() {
        return new InMemoryUserDetailsManager(
                User.withUsername("bob").password("{noop}bob").roles("USER", "AUDITOR").build());
    }
}
