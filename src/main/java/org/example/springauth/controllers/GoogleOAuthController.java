package org.example.springauth.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springauth.security.TokenService;
import org.example.springauth.usuario.Usuario;
import org.example.springauth.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2/login")
@Tag(name = "Oauth", description = "Controller com callback de sucesso para Oauth do Google")
public class GoogleOAuthController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Autenticação com Google OAuth2", description = "Realiza autenticação via OAuth2 e retorna um token JWT redirecionando para o frontend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirecionamento para o frontend com token JWT"),
            @ApiResponse(responseCode = "400", description = "Erro na autenticação com o Google",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro ao processar autenticação",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na autenticação com o Google");
        }

        try {
            OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Auth.getPrincipal();

            String email = oauth2User.getAttribute("email");
            String nome = oauth2User.getAttribute("name");

            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email não encontrado");
            }

            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseGet(() -> usuarioService.criarNovoUsuario(email, nome));

            String tokenJWT = tokenService.gerarToken(usuario);


            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:4200/oauth-success?token=" + tokenJWT)
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar autenticação: " + e.getMessage());
        }
    }
}


