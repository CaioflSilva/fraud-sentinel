package com.fraudsentinel.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final JwtBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var token = extractToken(request);
        log.info("JWT Filter: path={}, token present={}", request.getRequestURI(), token != null);

        if (token != null) {
            try {
                if (jwtTokenService.isValidToken(token)) {
                    var jti = jwtTokenService.getJti(token);

                    if (!blacklistService.isBlacklisted(jti)) {
                        var type = jwtTokenService.getTokenType(token);

                        if ("access".equals(type)) {
                            var email = jwtTokenService.getEmail(token);
                            var role = jwtTokenService.getRole(token);

                            var auth = new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                            SecurityContextHolder.getContext().setAuthentication(auth);
                            log.info("JWT Filter: authenticated user={}, role={}", email, role);
                        }
                    } else {
                        log.info("JWT Filter: token is blacklisted");
                    }
                } else {
                    log.info("JWT Filter: token is invalid");
                }
            } catch (Exception e) {
                log.error("JWT Filter: error processing token", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}