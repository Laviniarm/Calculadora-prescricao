// src/main/java/com/ifpb/cp/web/PublicController.java
package com.ifpb.cp.controller;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/ping")
    public Map<String, Object> ping(CsrfToken token) {
        // Só de injetar CsrfToken, o CookieCsrfTokenRepository emite o cookie XSRF-TOKEN
        return Map.of(
                "status", "ok",
                "csrfHeaderName", token.getHeaderName(),
                "csrfTokenPresent", token.getToken() != null,
                "csrfToken", token.getToken()
        );
    }
}