package org.example.springauth.model;

import org.example.springauth.usuario.Usuario;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private boolean twoFactorAuthenticationEnabled;

    public static List<UsuarioDTO> convertList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioDTO::convertToUsuarioDTO)
                .toList();
    }

    public static UsuarioDTO convertToUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTwoFactorAuthenticationEnabled(usuario.isTwoFactorAuthenticationEnabled());
        return dto;
    }
}
