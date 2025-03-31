package org.example.springauth.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.springauth.model.tarefa.Prioridade;
import org.example.springauth.model.tarefa.Situacao;
import org.example.springauth.model.tarefa.Tarefa;
import org.example.springauth.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository repository;

    public Tarefa save(Tarefa tarefa){
        return repository.save(tarefa);
    }

    public void delete(Tarefa tarefa){
        repository.delete(tarefa);
    }

    public List<Tarefa> findAll(Long numero, String titulo, String responsavel, Prioridade prioridade, Situacao situacao){
        return repository.findWithFilters(numero, titulo, responsavel, prioridade, situacao);
    }

    public Tarefa update(Tarefa tarefa) {

        if (!repository.existsById(tarefa.getId())) {
            throw new EntityNotFoundException("Tarefa não encontrada com o id: " + tarefa.getId());
        }

        return repository.saveAndFlush(tarefa);
    }

    public Tarefa getById(Long id){
        Optional<Tarefa> tarefa = repository.findById(id);
        return tarefa.orElse(null);

    }
}
