# 🚚 LogiRoute API

Sistema de backend para **roteamento inteligente de entregas logísticas**, desenvolvido com Spring Boot 3.x e Java 17.

## 📋 Problema de Negócio Resolvido

Em um centro de distribuição, pacotes chegam continuamente e precisam ser despachados para entregadores de forma eficiente. O sistema resolve:

- **Priorização automática**: pacotes PRIME e EXPRESSO são despachados antes dos NORMAL
- **Desempate justo**: quando dois pacotes têm o mesmo tipo de frete, o que chegou primeiro no galpão sai primeiro (FIFO)
- **Validação de capacidade**: o sistema impede despachos que excedam a capacidade de carga do entregador
- **Rastreamento de status**: ciclo de vida completo do pacote (AGUARDANDO → DESPACHADO → ENTREGUE)

## 🏗️ Arquitetura

A API segue uma arquitetura em camadas:

- **controller** → Camada REST (endpoints HTTP)
- **service** → Regras de negócio e algoritmo de roteamento
- **domain** → Entidades e Enums
- **dto** → Objetos de transferência (Records Java)
- **repository** → Acesso a dados (Spring Data JPA)
- **exception** → Tratamento global de erros

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Java 17 | Linguagem principal |
| Spring Boot 3.x | Framework web |
| Spring Data JPA | Persistência de dados |
| H2 Database | Banco em memória |
| Lombok | Redução de boilerplate |
| Bean Validation | Validação de DTOs |
| JUnit 5 + Mockito | Testes unitários |

## 🚀 Como Rodar o Projeto

Pré-requisitos: Java 17+ e Maven 3.8+

Clone o repositório, entre na pasta e rode:

    .\mvnw spring-boot:run

A API estará disponível em: http://localhost:8080

Console do H2: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:logiroutedb
- User: sa | Password: (vazio)

## 📡 Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| POST | /api/pacotes | Cadastra um novo pacote |
| GET | /api/logistica/rotas?cep={cep} | Retorna fila ordenada por prioridade |
| PUT | /api/pacotes/{id}/despachar | Despacha um pacote para um entregador |
| POST | /api/entregadores | Cadastra um novo entregador |

## ⚠️ Tratamento de Erros

Todos os erros retornam no formato padronizado com status, erro, mensagem e timestamp.

| Código | Situação |
|---|---|
| 400 | CEP inválido, peso excedido, campos obrigatórios ausentes |
| 404 | Pacote ou entregador não encontrado |
| 500 | Erro interno inesperado |

## 🧪 Testes

5 testes unitários cobrindo:

- Ordenação por prioridade de frete
- Filtro de pacotes AGUARDANDO
- Desempate por data de entrada
- Exceção para pacote inexistente
- Exceção para peso excedido

## 👤 Autor

**Luiz Arioza** — [LinkedIn](https://www.linkedin.com/in/luiz-arioza-ba64a1260/) | [GitHub](https://github.com/LuizArioza)