package br.com.fiap.restaurante.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "test-secret-key-minimum-32-chars-abcdef!!";
    private static final long EXPIRATION_MS = 3_600_000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    @Test
    void generateToken_deveGerarTokenNaoNulo() {
        String token = jwtService.generateToken("joao.silva", Map.of("userType", "CUSTOMER"));

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void generateToken_deveCriarTokensDistintos_paraLoginsDiferentes() {
        String token1 = jwtService.generateToken("joao.silva", Map.of());
        String token2 = jwtService.generateToken("maria.souza", Map.of());

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void extractLogin_deveRetornarLoginCorreto() {
        String token = jwtService.generateToken("joao.silva", Map.of());

        String login = jwtService.extractLogin(token);

        assertThat(login).isEqualTo("joao.silva");
    }

    @Test
    void extractExpiration_deveRetornarDataFutura() {
        String token = jwtService.generateToken("joao.silva", Map.of());

        Date expiration = jwtService.extractExpiration(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void getExpirationMs_deveRetornarValorConfigurado() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(EXPIRATION_MS);
    }

    @Test
    void isTokenValid_deveRetornarTrue_paraTokenValidoComLoginCorreto() {
        String token = jwtService.generateToken("joao.silva", Map.of());

        assertThat(jwtService.isTokenValid(token, "joao.silva")).isTrue();
    }

    @Test
    void isTokenValid_deveRetornarFalse_quandoLoginDiverge() {
        String token = jwtService.generateToken("joao.silva", Map.of());

        assertThat(jwtService.isTokenValid(token, "outro.login")).isFalse();
    }

    @Test
    void isTokenValid_deveRetornarFalse_paraTokenMalformado() {
        assertThat(jwtService.isTokenValid("token.invalido.xyz", "joao.silva")).isFalse();
    }

    @Test
    void isTokenValid_deveRetornarFalse_paraTokenExpirado() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken("joao.silva", Map.of());

        assertThat(jwtService.isTokenValid(token, "joao.silva")).isFalse();
    }

    @Test
    void extractClaim_deveRetornarClaimCorretamente() {
        String token = jwtService.generateToken("joao.silva", Map.of("userType", "OWNER"));

        String userType = jwtService.extractClaim(token, claims -> claims.get("userType", String.class));

        assertThat(userType).isEqualTo("OWNER");
    }

    @Test
    void extractClaim_deveRetornarNome_quandoClaimNomePresente() {
        String token = jwtService.generateToken("joao.silva", Map.of("nome", "João Silva"));

        String nome = jwtService.extractClaim(token, claims -> claims.get("nome", String.class));

        assertThat(nome).isEqualTo("João Silva");
    }
}
