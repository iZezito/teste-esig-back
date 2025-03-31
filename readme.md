# README - Projeto Java 21 + Spring Boot 3

## Requisitos
- Java 21
- Banco de dados PostgreSQL
- Maven.

## Funcionalidades Implementadas
Este projeto atende aos seguintes requisitos:
- **(a)** Criado com Angular 19
- **(b)** Backend desenvolvido com Java 21 e Spring Boot 3
- **(c)** Persistência com PostgreSQL e JPA
- **(d)** Endpoints REST
- **(e)** Autenticação com JWT
- **(g)** Documentação da API com Swagger

## Funcionalidades Adicionais
- Validação de e-mail ao criar conta;
- Recuperação de senha;
- Autenticação de dois fatores (2FA).
- Autenticação com o Google


## Sobre o Projeto
Este projeto foi desenvolvido como parte do processo de Rercutamento da [ESIG Group](https://esig.group). Trata-se de um **SISTEMA DE GESTÃO DE TAREFAS**, onde é possível:

- Criar uma tarefa
- Atualizar a tarefa
- Remover a tarefa
- Listar tarefas
- Concluir tarefa (mudando o status para "Concluída")

Cada tarefa possui os seguintes atributos:
- **Título**
- **Descrição**
- **Responsável**
- **Prioridade** (Alta, Média, Baixa)
- **Deadline** (Data de entrega)
- **Situação** (Em andamento ou Concluída)

A listagem permite filtros e exibe as ações necessárias para o gerenciamento das tarefas.

## Como Executar
1. Configurar PostgreSQL e criar um banco
2. Definir credenciais no `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/esig_teste
spring.datasource.username=postgres
spring.datasource.password=postgres
```
3. Compilar e rodar o projeto
```sh
mvn clean install
mvn spring-boot:run
```
Acesse a API: `http://localhost:8080/`
Swagger: `http://localhost:8080/swagger-ui.html`
