package org.example.springauth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springauth.security.DadosTokenJWT;
import org.example.springauth.security.TokenService;
import org.example.springauth.service.TwoFactorAuthService;
import org.example.springauth.usuario.DadosAutenticacao;
import org.example.springauth.usuario.Usuario;
import org.example.springauth.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticação", description = "Controller para autenticação")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Autenticação do usuário", description = "Autentica um usuário e retorna um token JWT se as credenciais forem válidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida",
                    content = @Content(schema = @Schema(implementation = DadosTokenJWT.class))),
            @ApiResponse(responseCode = "202", description = "Código de autenticação enviado para o e-mail"),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas ou código 2FA inválido",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "E-mail não validado",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
            var authentication = manager.authenticate(authenticationToken);
            var usuario = (Usuario) authentication.getPrincipal();

            if (!usuario.isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("E-mail não validado, cheque sua caixa de entrada!");
            }

            if (usuario.isTwoFactorAuthenticationEnabled()) {
                if (dados.codigo() == null || dados.codigo().isEmpty()) {
                    twoFactorAuthService.generateAndSend2FACode(usuario);
                    return ResponseEntity.accepted().body("Código de autenticação enviado para o e-mail.");
                }

                if (!twoFactorAuthService.validate2FACode(usuario, dados.codigo())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código 2FA inválido ou expirado.");
                }
            }

            String tokenJWT = tokenService.gerarToken(usuario);
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, usuario.getNome()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Credenciais inválidas. Por favor, verifique seu e-mail e senha e tente novamente.");
        }
    }
}