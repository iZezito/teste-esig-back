package org.example.springauth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.example.springauth.model.UsuarioCreateDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springauth.model.UsuarioDTO;
import org.example.springauth.model.auth.PasswordResetToken;
import org.example.springauth.repository.TokenRepository;
import org.example.springauth.service.EmailService;
import org.example.springauth.service.EmailVerificationService;
import org.example.springauth.usuario.Usuario;
import org.example.springauth.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Controller de Usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário e envia um e-mail de verificação")
    @PostMapping
    public ResponseEntity<String> insert(@RequestBody @Valid Usuario usuario){
        Usuario user = service.save(usuario);
        StringBuilder builder = new StringBuilder();
        String token = service.createVerificatioEmailToken(user);
        emailService.sendEmail(user.getEmail(), "Verificação de Conta", builder.append("Clique no link para validar seu email: ").append("http://localhost:4200/validate-email?token=").append(token).toString());
        return ResponseEntity.ok("Usuário registrado. Verifique seu e-mail para ativação.");
    }

    @Operation(summary = "Obter usuário autenticado", description = "Retorna os dados do usuário atualmente autenticado")
    @GetMapping("/")
    public ResponseEntity<UsuarioDTO> getUsuarioLogado(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        UsuarioDTO usuarioDTO = UsuarioDTO.convertToUsuarioDTO(usuario);
        return ResponseEntity.ok(usuarioDTO);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados do usuário autenticado")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@RequestBody UsuarioCreateDTO usuario,
                                             @PathVariable Long id,
                                             Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        if (!usuarioLogado.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuarioBanco = service.getById(id);
        if (usuarioBanco == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        usuarioBanco.setNome(usuario.getNome());
        usuarioBanco.setEmail(usuario.getEmail());
        usuarioBanco.setTwoFactorAuthenticationEnabled(usuario.isTwoFactorAuthenticationEnabled());

        service.update(usuarioBanco);

        UsuarioDTO usuarioDTO = UsuarioDTO.convertToUsuarioDTO(usuarioBanco);
        return ResponseEntity.ok(usuarioDTO);
    }

    @Operation(summary = "Verificar login", description = "Verifica se um usuário com o login fornecido existe")
    @GetMapping("/login/{login}")
    public Boolean login(@PathVariable String login){
        Usuario usuarioBanco = service.findByLogin(login);
        return usuarioBanco != null;
    }

    @Operation(summary = "Validar e-mail", description = "Verifica o e-mail de um usuário com um token de verificação")
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        if (emailVerificationService.verifyEmail(token)) {
            return ResponseEntity.ok("E-mail verificado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token de verificação inválido ou expirado.");
        }
    }

    @Operation(summary = "Solicitar redefinição de senha", description = "Envia um e-mail para redefinição de senha com um token")
    @PostMapping("/password-reset-tk")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        Optional<Usuario> userOpt = Optional.ofNullable(service.findByLogin(email));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }
        StringBuilder builder = new StringBuilder();
        Usuario user = userOpt.get();
        String token = service.createPasswordResetToken(user);
        emailService.sendEmail(user.getEmail(), "Redefinição de senha", builder.append("Clique no link para redefinir sua senha: ").append("http://localhost:4200/reset-password?token=").append(token).toString());

        return ResponseEntity.ok("E-mail de recuperação de senha enviado.");
    }

    @Operation(summary = "Redefinir senha", description = "Permite redefinir a senha de um usuário utilizando um token")
    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isEmpty() || resetTokenOpt.get().isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
        }

        Usuario user = resetTokenOpt.get().getUser();
        service.updatePassword(user, newPassword);
        tokenRepository.delete(resetTokenOpt.get());

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}
