# 🌳 NexusTree - Motor de Versionamento de Dados Estruturados

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=for-the-badge&logo=springboot)
![Gradle](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?style=for-the-badge&logo=gradle)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)

> 🖥️ **Interface Gráfica Disponível:** Este é o repositório do **Backend (Motor Core)**. Para visualizar e interagir com os dados através de um Dashboard moderno, acesse o repositório do Front-end: **[NexusTree Explorer](https://github.com/HigorPassarela/nexustree-explorer)**.

O NexusTree é uma API REST de alta performance que atua como um **"Git para Dados Estruturados"**. 

Em vez de sobrescrever dados em um banco de dados (`UPDATE table SET...`), o NexusTree rastreia as mudanças ao longo do tempo calculando a diferença matemática (*Diff*) entre os payloads JSON, gerando hashes criptográficos SHA-256 e armazenando apenas os deltas (JSON Patches). Isso permite que os usuários façam uma "viagem no tempo" e reconstruam o estado exato de um documento em qualquer ponto da história.

## 🚀 Principais Funcionalidades

*   **Compressão Delta (Diffing):** Calcula automaticamente adições, remoções e modificações entre JSONs, economizando uma quantidade massiva de armazenamento no banco de dados.
*   **Hashing Criptográfico:** Cada commit gera um hash SHA-256 único e imutável com base em seu conteúdo, hash pai e timestamp.
*   **Viagem no Tempo (Patching):** Reconstrói dados históricos em tempo real aplicando patches sequenciais desde o commit Gênesis até o hash solicitado.
*   **Arquitetura Hexagonal:** Separação estrita de responsabilidades usando *Ports & Adapters*. O domínio central é 100% Java puro, completamente desacoplado do Spring e de bancos de dados.
*   **Tratamento de Erros RFC 7807:** Respostas de erro da API padronizadas e profissionais (`ProblemDetail`).

## 🏗️ Arquitetura e Design Multi-Módulo

Este projeto foi construído usando uma configuração **Multi-Módulo no Gradle**, aplicando a **Arquitetura Hexagonal (Ports and Adapters)** para garantir o máximo de manutenibilidade e testabilidade.

```text
nexustree/
├── nexus-core/      # 🧠 O Cérebro. Java 21 puro. Sem dependências do Spring.
│                    # Contém os algoritmos de Diffing/Patching, Records de Domínio e Ports (Interfaces).
├── nexus-data/      # 🗄️ O Adaptador de Banco de Dados. 
│                    # Implementa os Ports centrais usando Spring Data JPA, Hibernate e PostgreSQL JSONB.
└── nexus-web/       # 🌐 O Adaptador Web e Orquestrador.
                     # Expõe os endpoints REST, configura o Swagger UI e gerencia as Exceções RFC 7807.
```

## 🛠️ Stack Tecnológica

*   **Linguagem:** Java 21 (Utilizando `Records`, `Pattern Matching` e estruturas de dados imutáveis).
*   **Framework:** Spring Boot 3.2+
*   **Banco de Dados:** PostgreSQL 16 (Aproveitando o tipo de dado nativo `JSONB` para alta performance).
*   **Migrações:** Flyway (Versionamento de banco de dados baseado em SQL).
*   **Infraestrutura:** Docker e Docker Compose.
*   **Documentação:** Springdoc OpenAPI (Swagger UI).

## ⚙️ Como Rodar Localmente

### Pré-requisitos
*   [Java 21 JDK](https://adoptium.net/) instalado.
*   [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando.

### Passo 1: Iniciar o Banco de Dados
Abra o seu terminal no diretório raiz do projeto e execute:
```bash
docker compose up -d
```
*Isso iniciará um contêiner do PostgreSQL 16 na porta 5432.*

### Passo 2: Rodar a Aplicação
Use o Gradle Wrapper para iniciar a aplicação Spring Boot:
```bash
# No Linux/Mac
./gradlew :nexus-web:bootRun

# No Windows
gradlew.bat :nexus-web:bootRun
```
*O Flyway criará automaticamente as tabelas necessárias no banco de dados durante a inicialização.*

### Passo 3: Acessar a API ou o Dashboard
*   **Documentação Swagger:** Navegue até 👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
*   **Dashboard Visual:** Siga as instruções no repositório do **[NexusTree Explorer](https://github.com/HigorPassarela/nexustree-explorer)** para subir a interface React.

---

## 📖 Exemplo de Uso da API

### 1. Criar o Commit Gênesis
Envie o payload JSON inicial para o repositório.
```http
POST /api/v1/repos/employee-data/commits
Content-Type: application/json

{
  "branch": "main",
  "message": "Initial setup",
  "data": {
    "name": "Test",
    "role": "Junior Developer",
    "active": true
  }
}
```
**Resposta:** Você receberá um `commit_hash` único (ex: `a1b2c3...`).

### 2. Atualizar os Dados (O Motor calcula o Diff)
Envie o JSON atualizado. O motor detectará que apenas o campo "role" mudou e armazenará apenas o patch matemático no banco.
```http
POST /api/v1/repos/employee-data/commits
Content-Type: application/json

{
  "branch": "main",
  "message": "Promoted to Senior",
  "data": {
    "name": "Test",
    "role": "Senior Developer",
    "active": true
  }
}
```
**Resposta:** Você receberá um novo `commit_hash` (ex: `z9y8x7...`).

### 3. Viagem no Tempo (Reconstruir o Estado)
Recupere o estado exato do JSON no momento do segundo commit.
```http
GET /api/v1/repos/commits/z9y8x7...
```
**Resposta:**
```json
{
  "name": "Test",
  "role": "Senior Developer",
  "active": true
}
```

## 🛡️ Tratamento de Erros
O NexusTree segue estritamente a especificação **RFC 7807** para respostas de erro. Por exemplo, enviar um payload sem nenhuma alteração resultará em um `422 Unprocessable Entity`:

```json
{
  "type": "https://nexustree.api/errors/no-changes",
  "title": "Unprocessable Entity",
  "status": 422,
  "detail": "No changes detected in the payload. Commit rejected.",
  "instance": "/api/repos/employee-data/commits",
  "timestamp": "2024-04-24T12:00:00Z"
}
```

## 📄 Licença
Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.