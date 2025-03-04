# Paybills API

API para gerenciamento de contas de usuários, incluindo autenticação via JWT e operações CRUD para contas. A aplicação é fácil de executar utilizando Docker e está documentada com Swagger para facilitar a exploração dos endpoints.

## Tecnologias Utilizadas

A Paybills API foi desenvolvida com as seguintes tecnologias:

- **Java 17** - Linguagem principal utilizada para desenvolvimento.
- **Spring Boot** - Framework para facilitar o desenvolvimento de aplicações Java.
- **Spring Security** - Utilizado para autenticação e autorização via JWT.
- **PostgreSQL** - Banco de dados relacional utilizado na aplicação.
- **Flyway** - Gerenciamento de migração do banco de dados.
- **Swagger (Springdoc OpenAPI)** - Documentação interativa da API.
- **Docker e Docker Compose** - Para facilitar a execução da aplicação e seus serviços.

---

## Como Executar com Docker

Siga os passos abaixo para rodar a aplicação utilizando Docker:

### Clone o repositório

```bash
git clone https://github.com/seu-usuario/paybills-api.git
cd paybills-api
```

### Suba a aplicação com Docker Compose

```bash
docker-compose up --build
```

Esse comando irá:
- Criar e iniciar um contêiner com a aplicação Spring Boot.
- Criar e iniciar um contêiner com um banco de dados PostgreSQL.
- Configurar automaticamente o banco de dados utilizando Flyway.

### Acesse a aplicação

- A API estará disponível em: [http://localhost:8080](http://localhost:8080)
- Acesse a documentação Swagger UI para testar os endpoints: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Autenticação e Segurança

A API utiliza JWT para autenticação e controle de acesso aos endpoints protegidos. Para autenticar:

1. Registre um usuário via `POST /auth/register`.
2. Faça login via `POST /auth/login`, que retornará um token JWT.
3. Utilize o token no cabeçalho `Authorization` para acessar endpoints protegidos.

---

## Para testar o endpoint de Importação de contas por CSV baixe o arquivo

[📂 Baixar Arquivo CSV](https://github.com/DiegoPriess/paybills-api/raw/master/src/test/resources/test-bills.csv)
💡 *Clique com o botão direito no link e selecione "Salvar link como..." para baixar o arquivo.*
