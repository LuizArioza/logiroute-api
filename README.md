\# 🚚 LogiRoute API



Sistema de backend para \*\*roteamento inteligente de entregas logísticas\*\*, desenvolvido com Spring Boot 3.x e Java 17.



\## 📋 Problema de Negócio Resolvido



Em um centro de distribuição, pacotes chegam continuamente e precisam ser despachados para entregadores de forma eficiente. O sistema resolve:



\- \*\*Priorização automática\*\*: pacotes PRIME e EXPRESSO são despachados antes dos NORMAL

\- \*\*Desempate justo\*\*: quando dois pacotes têm o mesmo tipo de frete, o que chegou primeiro no galpão sai primeiro (FIFO)

\- \*\*Validação de capacidade\*\*: o sistema impede despachos que excedam a capacidade de carga do entregador

\- \*\*Rastreamento de status\*\*: ciclo de vida completo do pacote (AGUARDANDO → DESPACHADO → ENTREGUE)



\---



\## 🏗️ Arquitetura



src/main/java/com/logiroute/api/

│

├── controller/          → Camada REST (endpoints HTTP)

│   └── LogisticaController

│

├── service/             → Regras de negócio

│   └── RoteamentoService

│

├── domain/              → Entidades e Enums

│   ├── Pacote

│   ├── Entregador

│   └── enums/

│       ├── TipoFrete    (PRIME, EXPRESSO, NORMAL)

│       └── StatusPacote (AGUARDANDO, DESPACHADO, ENTREGUE, CANCELADO)

│

├── dto/                 → Objetos de transferência (Records Java)

│   ├── PacoteRequestDTO

│   ├── PacoteResponseDTO

│   └── DespacharPacoteRequestDTO

│

├── repository/          → Acesso a dados (Spring Data JPA)

│   ├── PacoteRepository

│   └── EntregadorRepository

│

└── exception/           → Tratamento global de erros

├── GlobalExceptionHandler

├── ResourceNotFoundException

├── BusinessException

└── ErrorResponse

\---



\## 🛠️ Tecnologias Utilizadas



| Tecnologia | Versão | Uso |

|---|---|---|

| Java | 17 | Linguagem principal |

| Spring Boot | 3.5.x | Framework web |

| Spring Data JPA | 3.5.x | Persistência de dados |

| H2 Database | - | Banco em memória |

| Lombok | - | Redução de boilerplate |

| Bean Validation | - | Validação de DTOs |

| JUnit 5 | - | Testes unitários |

| Mockito | - | Mock de dependências |



\---



\## 🚀 Como Rodar o Projeto



\### Pré-requisitos

\- Java 17+

\- Maven 3.8+



\### Passos



```bash

\# Clone o repositório

git clone https://github.com/LuizArioza/logiroute-api.git



\# Entre na pasta

cd logiroute-api



\# Rode a aplicação

.\\mvnw spring-boot:run

```



A API estará disponível em: `http://localhost:8080`



Console do H2 (banco de dados): `http://localhost:8080/h2-console`

\- JDBC URL: `jdbc:h2:mem:logiroutedb`

\- User: `sa` | Password: \*(vazio)\*



\---



\## 📡 Endpoints



\### Pacotes



| Método | Endpoint | Descrição |

|---|---|---|

| POST | `/api/pacotes` | Cadastra um novo pacote |

| GET | `/api/logistica/rotas?cep={cep}` | Retorna fila ordenada por prioridade |

| PUT | `/api/pacotes/{id}/despachar` | Despacha um pacote para um entregador |



\### Entregadores



| Método | Endpoint | Descrição |

|---|---|---|

| POST | `/api/entregadores` | Cadastra um novo entregador |



\---



\## 📨 Exemplos de Requisições



\### Cadastrar Entregador

```json

POST /api/entregadores

{

&#x20;   "nome": "Carlos Silva",

&#x20;   "veiculo": "MOTO",

&#x20;   "capacidadeMaximaKg": 15.0,

&#x20;   "cepAtendimento": "01310-100"

}

```



\### Cadastrar Pacote

```json

POST /api/pacotes

{

&#x20;   "destinatario": "Bruno Costa",

&#x20;   "cepDestino": "01310-200",

&#x20;   "pesoKg": 5.0,

&#x20;   "tipoFrete": "PRIME",

&#x20;   "dataEntradaGalpao": "2026-05-27T09:00:00"

}

```



\### Consultar Fila de Entregas



GET /api/logistica/rotas?cep=01310



\### Despachar Pacote

```json

PUT /api/pacotes/1/despachar

{

&#x20;   "entregadorId": 1

}

```



\---



\## ⚠️ Tratamento de Erros



Todos os erros retornam no formato padronizado:



```json

{

&#x20;   "status": 404,

&#x20;   "erro": "Recurso não encontrado",

&#x20;   "mensagem": "Pacote não encontrado com id: 999",

&#x20;   "timestamp": "2026-05-27T20:26:10"

}

```



| Código | Situação |

|---|---|

| 400 | CEP inválido, peso excedido, campos obrigatórios ausentes |

| 404 | Pacote ou entregador não encontrado |

| 500 | Erro interno inesperado |



\---



\## 🧪 Testes



```bash

.\\mvnw test

```



\*\*5 testes unitários\*\* cobrindo:

\- ✅ Ordenação por prioridade de frete

\- ✅ Filtro de pacotes AGUARDANDO

\- ✅ Desempate por data de entrada

\- ✅ Exceção para pacote inexistente

\- ✅ Exceção para peso excedido



\---



\## 👤 Autor



\*\*Luiz Arioza\*\*

\- GitHub: \[@LuizArioza](https://github.com/LuizArioza)

