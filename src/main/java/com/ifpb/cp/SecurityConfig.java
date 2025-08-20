package com.ifpb.cp;

import com.ifpb.cp.repository.UsuarioRepository;
import com.ifpb.cp.security.JsonUsernamePasswordAuthFilter;
import com.ifpb.cp.security.RestAccessDeniedHandler;
import com.ifpb.cp.security.RestAuthEntryPoint;
import com.ifpb.cp.service.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final UsuarioRepository usuarioRepository; // <- novo

    private final RestAuthEntryPoint authEntryPoint = new RestAuthEntryPoint();
    private final AccessDeniedHandler accessDeniedHandler = new RestAccessDeniedHandler();


    public SecurityConfig(UsuarioDetailsService usuarioDetailsService, UsuarioRepository usuarioRepository) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(usuarioDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> logFilter() {
        FilterRegistrationBean<OncePerRequestFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                    throws ServletException, IOException {
                System.out.println("=== Debug Sessão ===");
                System.out.println("Session ID: " + request.getSession(false));
                System.out.println("Auth: " + request.getUserPrincipal());
                chain.doFilter(request, response);
            }
        });
        reg.addUrlPatterns("/prescricao/*");
        return reg;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager am) throws Exception {
        // Filtro que aceita JSON no /api/auth/login
        var jsonLoginFilter = new JsonUsernamePasswordAuthFilter("/api/auth/login", usuarioRepository);
        jsonLoginFilter.setAuthenticationManager(am);

        http
                .csrf(csrf -> csrf
                        // Em SPA, mantenha CSRF habilitado e exponha o token num cookie legível pelo JS
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/usuarios/**", "/api/auth/**", "/prescricao/calcular", "/prescricao/salvar")
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/logout", "/usuarios/**", "/public/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/prescricao/salvar").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                // Não usamos formLogin; preferimos nosso filtro que lê JSON
                .addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(lo -> lo
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                );

        return http.build();
    }
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> csrfDebug() {
        var reg = new FilterRegistrationBean<OncePerRequestFilter>();
        reg.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest req,
                    HttpServletResponse res,
                    FilterChain chain) throws ServletException, IOException {

                if (req.getRequestURI().startsWith("/prescricao/salvar")) {
                    var header = req.getHeader("X-XSRF-TOKEN");
                    var cookie = req.getCookies() != null
                            ? java.util.Arrays.stream(req.getCookies())
                            .filter(c -> "XSRF-TOKEN".equals(c.getName()))
                            .map(jakarta.servlet.http.Cookie::getValue)
                            .findFirst().orElse(null)
                            : null;
                    var attr = (org.springframework.security.web.csrf.CsrfToken)
                            req.getAttribute(org.springframework.security.web.csrf.CsrfToken.class.getName());

                    System.out.println("=== CSRF DEBUG /prescricao/salvar ===");
                    System.out.println("Header X-XSRF-TOKEN: " + header);
                    System.out.println("Cookie XSRF-TOKEN  : " + cookie);
                    System.out.println("ReqAttr CSRF token : " + (attr != null ? attr.getToken() : null));
                }
                chain.doFilter(req, res);
            }
        });
        reg.addUrlPatterns("/prescricao/*");
        reg.setOrder(0);
        return reg;
    }

    // Libere APENAS o origin do seu frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:5173", "https://seu-front.app")); // ajuste
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Content-Type","X-XSRF-TOKEN","X-Requested-With","Accept","Authorization"));
        cfg.setAllowCredentials(true); // necessário para cookie JSESSIONID
        cfg.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }


}



