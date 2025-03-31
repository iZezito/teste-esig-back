package org.example.springauth.model.tarefa;

import jakarta.persistence.*;
import lombok.*;
import org.example.springauth.usuario.Usuario;

import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    private String responsavel;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private Situacao situacao = Situacao.EM_ANDAMENTO;
}
