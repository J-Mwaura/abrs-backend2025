package com.soekm.abrs.security;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtToUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract the username (subject)
        String username = jwt.getSubject();

        // Extract the roles from the claim "authz.roles"
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        // Return a JwtAuthenticationToken with the extracted authorities
        return new JwtAuthenticationToken(jwt, authorities, username);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Get the claim "authz.roles"
        Map<String, Object> claims = jwt.getClaims();
        if (claims.containsKey("authz.roles")) {
            Object rolesClaim = claims.get("authz.roles");
            if (rolesClaim instanceof List) {
                List<String> roles = (List<String>) rolesClaim;
                return roles.stream()
                        .map(role -> {
                            // Ensure role has proper prefix
                            if (!role.startsWith("ROLE_")) {
                                return new SimpleGrantedAuthority("ROLE_" + role);
                            }
                            return new SimpleGrantedAuthority(role);
                        })
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }
}