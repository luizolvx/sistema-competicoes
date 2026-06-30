package br.edu.ifsp.competicoes_api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "ifsp-competicoes-segredo-super-secreto-2024-chave-longa-para-seguranca");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido e não vazio")
    void deveGerarTokenValido() {
        String token = jwtUtil.gerarToken("gustavo@email.com", "ROLE_USER");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Deve extrair corretamente o e-mail de um token válido")
    void deveExtrairEmailDoToken() {
        String token = jwtUtil.gerarToken("gustavo@email.com", "ROLE_USER");

        String emailExtraido = jwtUtil.extrairEmail(token);

        assertEquals("gustavo@email.com", emailExtraido);
    }

    @Test
    @DisplayName("Deve validar um token gerado corretamente como verdadeiro")
    void deveValidarTokenComoVerdadeiro() {
        String token = jwtUtil.gerarToken("gustavo@email.com", "ROLE_USER");

        assertTrue(jwtUtil.validarToken(token));
    }

    @Test
    @DisplayName("Deve invalidar um token corrompido/malformado")
    void deveInvalidarTokenCorrompido() {
        String tokenInvalido = "isso.nao.eh.um.token.valido";

        assertFalse(jwtUtil.validarToken(tokenInvalido));
    }
}