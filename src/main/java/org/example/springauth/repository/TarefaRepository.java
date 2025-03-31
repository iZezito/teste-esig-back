package org.example.springauth.repository;

import org.example.springauth.model.tarefa.Prioridade;
import org.example.springauth.model.tarefa.Situacao;
import org.example.springauth.model.tarefa.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    @Query("SELECT t FROM Tarefa t WHERE " +
            "(:titulo IS NULL OR (LOWER(t.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) OR LOWER(t.descricao) LIKE LOWER(CONCAT('%', :titulo, '%')))) AND " +
            "(:responsavel IS NULL OR LOWER(t.responsavel) LIKE LOWER(CONCAT('%', :responsavel, '%'))) AND " +
            "(:prioridade IS NULL OR t.prioridade = :prioridade) AND " +
            "(:situacao IS NULL OR t.situacao = :situacao) AND " +
            "(:numero IS NULL OR t.id= :numero)"
    )
    List<Tarefa> findWithFilters(
            @Param("numero") Long numero,
            @Param("titulo") String titulo,
            @Param("responsavel") String responsavel,
            @Param("prioridade") Prioridade prioridade,
            @Param("situacao") Situacao situacao
    );
}
