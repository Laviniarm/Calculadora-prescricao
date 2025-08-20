package com.ifpb.cp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifpb.cp.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.IOException;
import java.util.Map;

public class JsonUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonUsernamePasswordAuthFilter(String loginUrl, UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

//    private void onSuccess(HttpServletRequest req, HttpServletResponse res,
//                           Authentication auth) throws IOException {
//        res.setStatus(HttpServletResponse.SC_OK);
//        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        var principal = (UserDetails) auth.getPrincipal();
//        mapper.writeValue(res.getOutputStream(), Map.of(
//                "status", "OK",
//                "username", principal.getUsername(),
//                "authorities", principal.getAuthorities()
//        ));
//    }
    private final UsuarioRepository usuarioRepository; // injete via construtor

//    private void onSuccess(HttpServletRequest req, HttpServletResponse res,
//                           Authentication auth) throws IOException {
//        res.setStatus(HttpServletResponse.SC_OK);
//        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        var ud = (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
//        var usuarioOpt = usuarioRepository.findByEmail(ud.getUsername());
//
//        Map<String, Object> body;
//        if (usuarioOpt.isPresent()) {
//            var u = usuarioOpt.get();
//            body = Map.of(
//                    "status", "OK",
//                    "usuario", new com.ifpb.cp.dto.UsuarioResponse(u.getId(), u.getNome(), u.getEmail())
//            );
//        } else {
//            body = Map.of("status", "OK", "usuario", null);
//        }
//
//        mapper.writeValue(res.getOutputStream(), body);
//    }

    private void onSuccess(HttpServletRequest req, HttpServletResponse res,
                           Authentication auth) throws IOException {
        req.getSession(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
        new HttpSessionSecurityContextRepository()
                .saveContext(SecurityContextHolder.getContext(), req, res);

        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var ud = (UserDetails) auth.getPrincipal();

        var usuarioResp = usuarioRepository.findByEmail(ud.getUsername())
                .map(u -> new com.ifpb.cp.dto.UsuarioResponse(u.getId(), u.getNome(), u.getEmail()))
                // evita null no Map.of: devolve um DTO “mínimo”
                .orElse(new com.ifpb.cp.dto.UsuarioResponse(null, null, ud.getUsername()));

        var body = Map.of(
                "status", "OK",
                "usuario", usuarioResp
        );

        mapper.writeValue(res.getOutputStream(), body);
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
