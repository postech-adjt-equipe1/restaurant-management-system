package br.com.fiap.restaurante.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveContinuarSemAutenticar_quandoHeaderAuthorizationAusente() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).extractLogin(any());
    }

    @Test
    void deveContinuarSemAutenticar_quandoHeaderNaoComecaComBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).extractLogin(any());
    }

    @Test
    void deveAutenticar_quandoTokenValidoComUserTypeCUSTOMER() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
        when(jwtService.extractLogin("valid.token")).thenReturn("joao.silva");
        when(jwtService.isTokenValid("valid.token", "joao.silva")).thenReturn(true);
        when(jwtService.extractClaim(eq("valid.token"), any())).thenReturn("CUSTOMER");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("joao.silva");
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_CUSTOMER");
    }

    @Test
    void deveAutenticar_quandoTokenValidoComUserTypeOWNER() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer owner.token");
        when(jwtService.extractLogin("owner.token")).thenReturn("maria.souza");
        when(jwtService.isTokenValid("owner.token", "maria.souza")).thenReturn(true);
        when(jwtService.extractClaim(eq("owner.token"), any())).thenReturn("OWNER");

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("maria.souza");
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_OWNER");
    }

    @Test
    void deveDefinirRoleUser_quandoTokenValidoSemUserType() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token.sem.tipo");
        when(jwtService.extractLogin("token.sem.tipo")).thenReturn("joao.silva");
        when(jwtService.isTokenValid("token.sem.tipo", "joao.silva")).thenReturn(true);
        when(jwtService.extractClaim(eq("token.sem.tipo"), any())).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void naoDeveAutenticar_quandoTokenInvalido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
        when(jwtService.extractLogin("invalid.token")).thenReturn("joao.silva");
        when(jwtService.isTokenValid("invalid.token", "joao.silva")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void naoDeveAutenticar_quandoLoginExtaidoForNulo() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer qualquer.token");
        when(jwtService.extractLogin("qualquer.token")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).isTokenValid(any(), any());
    }

    @Test
    void deveContinuarSemAutenticar_quandoExcecaoLancadaAoProcessarToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer malformed.token");
        when(jwtService.extractLogin("malformed.token")).thenThrow(new RuntimeException("parse error"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
