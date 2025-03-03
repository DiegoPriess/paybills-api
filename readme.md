# Paybills API

API para gerenciamento de contas de usuários, com autenticação via JWT e operações CRUD para contas. A aplicação é fácil de executar usando Docker e está documentada com Swagger.

## Como Executar com Docker

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/seu-usuario/paybills-api.git
   cd paybills-api
   
2. **Suba a aplicação com Docker Compose:**

   ```bash
   docker-compose up --build

- Isso vai:
   - Subir a aplicação Spring Boot.
   - Subir um banco de dados PostgreSQL.
   - Configurar automaticamente o banco de dados.

3. **Acesse a aplicação:**
   - A API estará disponível em: http://localhost:8080.
   - Acesse o Swagger UI para testar os endpoints: http://localhost:8080/swagger-ui.html.