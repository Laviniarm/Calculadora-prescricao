package com.ifpb.cp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Map;

public class JsonUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonUsernamePasswordAuthFilter(String loginUrl) {
        setFilterProcessesUrl(loginUrl); // e.g. /api/auth/login
        setAuthenticationSuccessHandler(this::onSuccess);
        setAuthenticationFailureHandler(this::onFailure);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {

        // Tenta ler JSON {"username":"...", "password":"..."}
        String username;
        String password;
        try {
            var tree = mapper.readTree(req.getInputStream());
            username = tree.path("username").asText("");
            password = tree.path("password").asText("");
        } catch (IOException e) {
            username = req.getParameter("username"); // fallback form
            password = req.getParameter("password");
        }

        var authRequest = new UsernamePasswordAuthenticationToken(username, password);
        setDetails(req, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private void onSuccess(HttpServletRequest req, HttpServletResponse res,
                           Authentication auth) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var principal = (UserDetails) auth.getPrincipal();
        mapper.writeValue(res.getOutputStream(), Map.of(
                "status", "OK",
                "username", principal.getUsername(),
                "authorities", principal.getAuthorities()
        ));
    }

    private void onFailure(HttpServletRequest req, HttpServletResponse res,
                           AuthenticationException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getOutputStream(), Map.of(
                "status", "ERROR",
                "message", "Credenciais inválidas"
        ));
    }
}
