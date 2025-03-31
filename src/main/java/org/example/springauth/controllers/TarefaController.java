package org.example.springauth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springauth.model.tarefa.Prioridade;
import org.example.springauth.model.tarefa.Situacao;
import org.example.springauth.model.tarefa.Tarefa;
import org.example.springauth.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
@Tag(name = "Tarefas", description = "Gerenciamento de tarefas")
public class TarefaController {

    @Autowired
    private TarefaService service;

    @PostMapping
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa e retorna a entidade salva.")
    public Tarefa insert(@RequestBody Tarefa tarefa) {
        return service.save(tarefa);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna uma tarefa específica com base no ID fornecido.")
    public ResponseEntity<Tarefa> findById(@PathVariable Long id) {
        Tarefa tarefa = service.getById(id);
        if (tarefa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(tarefa);
    }

    @GetMapping
    @Operation(summary = "Listar tarefas com filtros", description = "Retorna uma lista de tarefas com opção de filtros por título, responsável, prioridade e situação.")
    public ResponseEntity<List<Tarefa>> findAll(
            @RequestParam(required = false) Long numero,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String responsavel,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Situacao situacao) {

        List<Tarefa> tarefas = service.findAll(numero,titulo, responsavel, prioridade, situacao);

        if (tarefas == null || tarefas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(tarefas);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma tarefa", description = "Atualiza uma tarefa existente e retorna a entidade atualizada.")
    public ResponseEntity<Tarefa> update(@RequestBody Tarefa tarefa, @PathVariable Long id) {
        tarefa.setId(id);
        Tarefa tarefaAtualizada = service.update(tarefa);
        return ResponseEntity.ok().body(tarefaAtualizada);
    }

    @PutMapping("/{id}/concluir")
    @Operation(summary = "Concluir a tarefa", description = "Atualiza a situação da tarefa para concluida!")
    public ResponseEntity<?> concluir(@PathVariable Long id) {
        Tarefa tarefa = service.getById(id);
        if (tarefa == null) {
            return ResponseEntity.notFound().build();
        }
        tarefa.setSituacao(Situacao.CONCLUIDA);
        service.update(tarefa);
        return ResponseEntity.ok().build();
    }



    @DeleteMapping(path = {"/{id}"})
    @Operation(summary = "Excluir uma tarefa", description = "Exclui uma tarefa com base no ID fornecido.")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Tarefa tarefa = service.getById(id);
        if (tarefa == null) {
            return ResponseEntity.notFound().build();
        }
        service.delete(tarefa);
        return ResponseEntity.ok().build();
    }
}

