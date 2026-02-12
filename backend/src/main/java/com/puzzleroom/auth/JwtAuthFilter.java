package com.puzzleroom.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length()).trim();

            // Only set auth if not already authenticated
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    Claims claims = jwtUtil.parseClaims(token);

                    String userId = claims.getSubject(); // sub
                    String email = claims.get("email", String.class);

                    // principal = userId; we also attach email in details
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null,
                            org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_USER"));


                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // You can also store email in request attribute for controllers if needed:
                    request.setAttribute("email", email);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception ignored) {
                    // Invalid token -> just don't authenticate; Security will reject protected routes
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
